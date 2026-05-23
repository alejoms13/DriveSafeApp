package miguel.alejandro.edu.drivesafeam.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.face.Face
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import miguel.alejandro.edu.drivesafeam.data.model.Alerta
import miguel.alejandro.edu.drivesafeam.data.model.ConfiguracionAlerta
import miguel.alejandro.edu.drivesafeam.data.model.ContactoEmergencia
import miguel.alejandro.edu.drivesafeam.data.model.EstadoDeteccion
import miguel.alejandro.edu.drivesafeam.data.model.NivelAlerta
import miguel.alejandro.edu.drivesafeam.data.model.TipoAccionEmergencia
import miguel.alejandro.edu.drivesafeam.data.model.TipoAlerta
import miguel.alejandro.edu.drivesafeam.data.repository.AlertaRepository
import miguel.alejandro.edu.drivesafeam.data.repository.AuthRepository
import miguel.alejandro.edu.drivesafeam.data.repository.ConfiguracionRepository
import miguel.alejandro.edu.drivesafeam.data.repository.UbicacionRepository
import miguel.alejandro.edu.drivesafeam.data.repository.UsuarioRepository
import miguel.alejandro.edu.drivesafeam.detection.AlertaManager
import miguel.alejandro.edu.drivesafeam.detection.AudioAlertManager

class MonitoreoViewModel(application: Application) : AndroidViewModel(application) {

    // ── Repositorios ─────────────────────────────────────────────
    private val authRepository = AuthRepository()
    private val alertaRepository = AlertaRepository()
    private val ubicacionRepository = UbicacionRepository(application)
    private val configuracionRepository = ConfiguracionRepository()
    private val usuarioRepository = UsuarioRepository()

    // ── Managers de alertas ───────────────────────────────────────
    /** Vibración + alarma del sistema (legado, se mantiene para compatibilidad). */
    private val alertaManager = AlertaManager(application)
    /** Nuevo manager de audio con archivos locales y callbacks de intent. */
    private val audioAlertManager = AudioAlertManager(application)

    // ── Estado de detección (expuesto a la UI) ────────────────────
    private val _estadoDeteccion = MutableStateFlow<EstadoDeteccion>(EstadoDeteccion.Seguro)
    val estadoDeteccion: StateFlow<EstadoDeteccion> = _estadoDeteccion

    private val _isFlashActive = MutableStateFlow(false)
    val isFlashActive: StateFlow<Boolean> = _isFlashActive

    // ── Control del protocolo SOS ─────────────────────────────────
    private var sosJob: Job? = null
    private var criticalStartTime = 0L
    private var accionEjecutada = false          // reemplaza whatsappSent + callSent

    // ── Contadores de frames ──────────────────────────────────────
    private var noFaceFrames = 0
    private var eyesClosedFrames = 0
    private var distractedFrames = 0
    private var safeFrames = 0

    // ── Umbrales de detección ─────────────────────────────────────
    private val UMBRAL_OJOS_CERRADOS = 3     // ~0.6 s a 5 fps → Peligro
    private val UMBRAL_DISTRACCION = 5       // ~1.0 s a 5 fps → Advertencia
    private val UMBRAL_SIN_ROSTRO = 10       // ~2.0 s a 5 fps → SinRostro
    private val UMBRAL_VOLVER_SEGURO = 10    // ~2.0 s seguros para resetear

    // ═══════════════════════════════════════════════════════════════
    //  ALGORITMO DE DETECCIÓN (ML Kit → Máquina de estados)
    // ═══════════════════════════════════════════════════════════════

    fun procesarRostros(faces: List<Face>) {
        if (faces.isEmpty()) {
            noFaceFrames++
            safeFrames = 0
            if (noFaceFrames > UMBRAL_SIN_ROSTRO) {
                cambiarEstado(EstadoDeteccion.SinRostro, TipoAlerta.NO_FACE)
            }
            return
        }

        noFaceFrames = 0
        val face = faces.first()

        val leftEyeOpen = face.leftEyeOpenProbability ?: 1.0f
        val rightEyeOpen = face.rightEyeOpenProbability ?: 1.0f
        val headEulerY = face.headEulerAngleY
        val headEulerZ = face.headEulerAngleZ

        when {
            // Somnolencia: ambos ojos < 20% apertura durante umbral de frames
            leftEyeOpen < 0.2f && rightEyeOpen < 0.2f -> {
                eyesClosedFrames++
                distractedFrames = 0
                safeFrames = 0
                if (eyesClosedFrames > UMBRAL_OJOS_CERRADOS) {
                    cambiarEstado(EstadoDeteccion.Peligro, TipoAlerta.DROWSINESS)
                }
            }
            // Distracción: rotación de cabeza > 20° en cualquier eje
            Math.abs(headEulerY) > 20 || Math.abs(headEulerZ) > 20 -> {
                distractedFrames++
                eyesClosedFrames = 0
                safeFrames = 0
                if (distractedFrames > UMBRAL_DISTRACCION) {
                    cambiarEstado(EstadoDeteccion.Advertencia, TipoAlerta.DISTRACTION)
                }
            }
            // Recuperación: ojos abiertos + cabeza centrada durante umbral de histéresis
            else -> {
                eyesClosedFrames = 0
                distractedFrames = 0
                safeFrames++
                if (safeFrames > UMBRAL_VOLVER_SEGURO && _estadoDeteccion.value != EstadoDeteccion.Seguro) {
                    _estadoDeteccion.value = EstadoDeteccion.Seguro
                    _isFlashActive.value = false
                    alertaManager.stopAlertas()
                    audioAlertManager.stopAll()
                    stopSOSTimer()
                }
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  CAMBIO DE ESTADO + AUDIO PREVENTIVO
    // ═══════════════════════════════════════════════════════════════

    private fun cambiarEstado(nuevoEstado: EstadoDeteccion, tipoAlerta: TipoAlerta) {
        if (_estadoDeteccion.value == nuevoEstado) return
        _estadoDeteccion.value = nuevoEstado

        val nivel = when (nuevoEstado) {
            EstadoDeteccion.Advertencia -> NivelAlerta.ADVERTENCIA
            EstadoDeteccion.Peligro     -> NivelAlerta.PELIGRO
            EstadoDeteccion.Critico     -> NivelAlerta.CRITICO
            else                        -> NivelAlerta.ADVERTENCIA
        }

        if (nuevoEstado != EstadoDeteccion.Seguro) {
            _isFlashActive.value = true

            val sharedPrefs = getApplication<Application>()
                .getSharedPreferences("ConfigAutoSave", android.content.Context.MODE_PRIVATE)
            val vibracionActiva = sharedPrefs.getBoolean("vibracionActiva", true)
            val sonidoActivo = sharedPrefs.getBoolean("sonidoActivo", true)

            // ── Audio preventivo (voz generada por TTS) ──
            audioAlertManager.playAtencionVolante()

            // ── Alerta tradicional (sonido de alarma fuerte y/o vibración) ──
            alertaManager.triggerAlerta(nivel, vibracionActiva, sonidoActivo)

            // ── Iniciar timer SOS solo si no está corriendo ──────
            if (sosJob == null) startSOSTimer()
        } else {
            _isFlashActive.value = false
            stopSOSTimer()
        }

        registrarAlerta(tipoAlerta, nivel)
    }

    // ═══════════════════════════════════════════════════════════════
    //  PROTOCOLO SOS — ACCIÓN ÚNICA EXCLUSIVA (Tarea 4)
    // ═══════════════════════════════════════════════════════════════

    private fun startSOSTimer() {
        criticalStartTime = System.currentTimeMillis()
        accionEjecutada = false

        sosJob = viewModelScope.launch {
            val sharedPrefs = getApplication<Application>()
                .getSharedPreferences("ConfigAutoSave", android.content.Context.MODE_PRIVATE)

            // Lee configuración de acción de emergencia guardada localmente
            val accionStr = sharedPrefs.getString("accionEmergencia", TipoAccionEmergencia.WHATSAPP.name)
            val accion = runCatching { TipoAccionEmergencia.valueOf(accionStr ?: "") }
                .getOrDefault(TipoAccionEmergencia.WHATSAPP)
            val secondsBeforeAction = sharedPrefs.getInt("secondsBeforeAction", 15)

            val nombre = sharedPrefs.getString("contactoNombre", "") ?: ""
            val telefono = sharedPrefs.getString("contactoTelefono", "") ?: ""
            val contacto = ContactoEmergencia(nombre, telefono)

            while (isActive) {
                val elapsedSeconds = (System.currentTimeMillis() - criticalStartTime) / 1000.0

                if (!accionEjecutada && elapsedSeconds >= secondsBeforeAction && accion != TipoAccionEmergencia.NINGUNA) {
                    accionEjecutada = true
                    // Delegar al método correcto según la acción configurada
                    when (accion) {
                        TipoAccionEmergencia.WHATSAPP -> lanzarAccionWhatsapp(contacto)
                        TipoAccionEmergencia.LLAMADA  -> lanzarAccionLlamada(contacto)
                        TipoAccionEmergencia.NINGUNA  -> { /* Solo alertas locales, ya activadas */ }
                    }
                    break // Ya ejecutamos la única acción → terminamos el loop
                }

                delay(1000)
            }
        }
    }

    private fun stopSOSTimer() {
        sosJob?.cancel()
        sosJob = null
        criticalStartTime = 0L
    }

    // ═══════════════════════════════════════════════════════════════
    //  ACCIONES DE EMERGENCIA CON AUDIO DE CONFIRMACIÓN (Tareas 2 y 3)
    // ═══════════════════════════════════════════════════════════════

    /**
     * Reproduce "activa_whatsapp.mp3" y, al terminar, construye y lanza
     * el Intent de WhatsApp con el mensaje formateado (Tarea 2).
     */
    private fun lanzarAccionWhatsapp(contacto: ContactoEmergencia) {
        if (contacto.telefono.isEmpty()) return

        // Reproducir audio de confirmación primero, luego lanzar el intent
        audioAlertManager.playActivaWhatsapp {
            // Este bloque se ejecuta cuando el audio termina de sonar
            viewModelScope.launch {
                val location = ubicacionRepository.getLastLocation().getOrNull()
                val locationText = if (location != null) {
                    "https://maps.google.com/?q=${location.latitude},${location.longitude}"
                } else {
                    "Ubicación no disponible"
                }

                // ── Tarea 2: Formato enriquecido con estilos de WhatsApp ──
                val sharedPrefs = getApplication<Application>()
                    .getSharedPreferences("ConfigAutoSave", android.content.Context.MODE_PRIVATE)
                
                // Intentamos sacar el nombreUsuario, si no está, usamos el usuario del correo, y si no, un genérico.
                val fallbackNombre = "un usuario de DriveSafe"
                var nombreUsuario = sharedPrefs.getString("nombreUsuario", "") ?: ""
                if (nombreUsuario.isBlank()) {
                    nombreUsuario = fallbackNombre
                }

                val mensajeFormateado = buildString {
                    append("¡Hola, ${contacto.nombre}! 👋 ")
                    append("Soy *${nombreUsuario}*. ")
                    append("_Este es un mensaje de emergencia automático de la app *DriveSafe*._ ")
                    append("Me encuentro en una situación de peligro al volante. Mi ubicación actual es: ")
                    append(locationText)
                }

                val telefonoLimpio = contacto.telefono.replace(" ", "")
                val mensajeCodificado = java.net.URLEncoder.encode(mensajeFormateado, "UTF-8")
                val url = "https://wa.me/$telefonoLimpio?text=$mensajeCodificado"

                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    getApplication<Application>().startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Reproduce "activa_llamada.mp3" y, al terminar, lanza el Intent de llamada.
     */
    private fun lanzarAccionLlamada(contacto: ContactoEmergencia) {
        if (contacto.telefono.isEmpty()) return

        audioAlertManager.playActivaLlamada {
            val telefonoLimpio = contacto.telefono.replace(" ", "")
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$telefonoLimpio")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                getApplication<Application>().startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  REGISTRO DE ALERTAS EN FIRESTORE
    // ═══════════════════════════════════════════════════════════════

    private fun registrarAlerta(tipo: TipoAlerta, nivel: NivelAlerta) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            val location = ubicacionRepository.getLastLocation().getOrNull()
            val alerta = Alerta(
                usuarioId = userId,
                tipo = tipo,
                nivel = nivel,
                latitud = location?.latitude,
                longitud = location?.longitude
            )
            alertaRepository.saveAlerta(alerta)
        }
    }

    // ═══════════════════════════════════════════════════════════════
    //  RESETEO COMPLETO
    // ═══════════════════════════════════════════════════════════════

    fun resetearEstado() {
        _estadoDeteccion.value = EstadoDeteccion.Seguro
        noFaceFrames = 0
        eyesClosedFrames = 0
        distractedFrames = 0
        safeFrames = 0
        _isFlashActive.value = false
        alertaManager.stopAlertas()
        audioAlertManager.stopAll()
        stopSOSTimer()
    }
}
