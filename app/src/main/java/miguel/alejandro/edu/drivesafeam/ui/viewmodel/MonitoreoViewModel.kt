package miguel.alejandro.edu.drivesafeam.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.face.Face
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import miguel.alejandro.edu.drivesafeam.data.model.Alerta
import miguel.alejandro.edu.drivesafeam.data.model.EstadoDeteccion
import miguel.alejandro.edu.drivesafeam.data.model.NivelAlerta
import miguel.alejandro.edu.drivesafeam.data.model.TipoAlerta
import miguel.alejandro.edu.drivesafeam.data.repository.AlertaRepository
import miguel.alejandro.edu.drivesafeam.data.repository.AuthRepository
import miguel.alejandro.edu.drivesafeam.data.repository.UbicacionRepository
import miguel.alejandro.edu.drivesafeam.detection.AlertaManager

class MonitoreoViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()
    private val alertaRepository = AlertaRepository()
    private val ubicacionRepository = UbicacionRepository(application)
    private val alertaManager = AlertaManager(application)

    private val _estadoDeteccion = MutableStateFlow<EstadoDeteccion>(EstadoDeteccion.Seguro)
    val estadoDeteccion: StateFlow<EstadoDeteccion> = _estadoDeteccion

    private var noFaceFrames = 0
    private var eyesClosedFrames = 0
    private var distractedFrames = 0

    // Constantes de umbral
    private val UMBRAL_OJOS_CERRADOS = 15 // Aprox 2s a 7.5fps
    private val UMBRAL_DISTRACCION = 22   // Aprox 3s a 7.5fps
    private val UMBRAL_SIN_ROSTRO = 30    // Aprox 4s a 7.5fps

    fun procesarRostros(faces: List<Face>) {
        if (faces.isEmpty()) {
            noFaceFrames++
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

        // Lógica simple de somnolencia
        if (leftEyeOpen < 0.2f && rightEyeOpen < 0.2f) {
            eyesClosedFrames++
            distractedFrames = 0
            if (eyesClosedFrames > UMBRAL_OJOS_CERRADOS) {
                cambiarEstado(EstadoDeteccion.Peligro, TipoAlerta.DROWSINESS)
            }
        } else if (Math.abs(headEulerY) > 20 || Math.abs(headEulerZ) > 20) {
            distractedFrames++
            eyesClosedFrames = 0
            if (distractedFrames > UMBRAL_DISTRACCION) {
                cambiarEstado(EstadoDeteccion.Advertencia, TipoAlerta.DISTRACTION)
            }
        } else {
            eyesClosedFrames = 0
            distractedFrames = 0
            _estadoDeteccion.value = EstadoDeteccion.Seguro
            alertaManager.stopAlertas()
        }
    }

    private fun cambiarEstado(nuevoEstado: EstadoDeteccion, tipoAlerta: TipoAlerta) {
        if (_estadoDeteccion.value == nuevoEstado) return

        _estadoDeteccion.value = nuevoEstado
        
        val nivel = when (nuevoEstado) {
            EstadoDeteccion.Advertencia -> NivelAlerta.ADVERTENCIA
            EstadoDeteccion.Peligro -> NivelAlerta.PELIGRO
            EstadoDeteccion.Critico -> NivelAlerta.CRITICO
            else -> NivelAlerta.ADVERTENCIA
        }

        alertaManager.triggerAlerta(nivel, vibracionActivada = true, sonidoActivado = true)
        registrarAlerta(tipoAlerta, nivel)
    }

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
}
