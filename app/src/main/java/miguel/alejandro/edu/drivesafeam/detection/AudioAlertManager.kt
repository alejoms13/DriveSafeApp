package miguel.alejandro.edu.drivesafeam.detection

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale
import java.util.UUID

/**
 * AudioAlertManager — Sistema de alertas de audio (TextToSpeech).
 *
 * Emplea el motor TTS nativo de Android (Google) para leer las advertencias
 * en tiempo real sin requerir archivos MP3 externos.
 */
class AudioAlertManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private var pendingCallback: (() -> Unit)? = null

    init {
        tts = TextToSpeech(context, this)
    }

    // Intentamos sacar el nombreUsuario, si no está, usamos el usuario del correo, y si no, un genérico.
    // 1. Definimos el nombre de respaldo
    private val fallbackNombre = "un usuario de DriveSafe"

    // 2. Obtenemos el nombre directamente usando el 'context' que ya tienes en el constructor
    private val nombreUsuario: String by lazy {
        val sharedPrefs = context.getSharedPreferences("ConfigAutoSave", Context.MODE_PRIVATE)
        val nombre = sharedPrefs.getString("nombreUsuario", "") ?: ""

        if (nombre.isBlank()) fallbackNombre else nombre
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Intentar configurar en español
            val result = tts?.setLanguage(Locale("es", "ES"))
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isTtsInitialized = true
            }

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}

                override fun onDone(utteranceId: String?) {
                    pendingCallback?.invoke()
                    pendingCallback = null
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    pendingCallback?.invoke()
                    pendingCallback = null
                }
            })
        }
    }

    fun playAtencionVolante() {
        speakText("Por favor ${nombreUsuario} presta atención al volante", null)
    }

    fun playActivaWhatsapp(onReady: () -> Unit) {
        speakText("Ejecutando mensaje de emergencia de WhatsApp", onReady)
    }

    fun playActivaLlamada(onReady: () -> Unit) {
        speakText("Ejecutando llamada de emergencia", onReady)
    }

    private fun speakText(text: String, onCompletion: (() -> Unit)?) {
        if (!isTtsInitialized || tts == null) {
            // Si el TTS no está listo o falló, ejecutar callback de inmediato para no bloquear emergencias
            onCompletion?.invoke()
            return
        }

        pendingCallback = onCompletion
        val utteranceId = UUID.randomUUID().toString()
        
        val params = Bundle().apply {
            putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, android.media.AudioManager.STREAM_ALARM)
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun stopAll() {
        if (isTtsInitialized) {
            tts?.stop()
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
