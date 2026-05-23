package miguel.alejandro.edu.drivesafeam.detection

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import miguel.alejandro.edu.drivesafeam.data.model.NivelAlerta

class AlertaManager(private val context: Context) {
    
    private var mediaPlayer: MediaPlayer? = null
    
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun triggerAlerta(nivel: NivelAlerta, vibracionActivada: Boolean, sonidoActivado: Boolean) {
        if (vibracionActivada) {
            when (nivel) {
                NivelAlerta.ADVERTENCIA -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(500)
                    }
                }
                NivelAlerta.PELIGRO, NivelAlerta.CRITICO -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val timings = longArrayOf(0, 500, 200, 500)
                        val amplitudes = intArrayOf(0, 255, 0, 255)
                        vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, 0))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(longArrayOf(0, 500, 200, 500), 0)
                    }
                }
            }
        }

        if (sonidoActivado) {
            playSonidoAlerta(nivel)
        }
    }

    private fun playSonidoAlerta(nivel: NivelAlerta) {
        if (mediaPlayer?.isPlaying == true) return

        try {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, uri)
                setAudioAttributes(
                    android.media.AudioAttributes.Builder()
                        .setUsage(android.media.AudioAttributes.USAGE_ALARM)
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = (nivel == NivelAlerta.PELIGRO || nivel == NivelAlerta.CRITICO)
                prepare()
                start()
                setOnCompletionListener {
                    it.release()
                    mediaPlayer = null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback seguro en caso de error
            val fallbackUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            mediaPlayer = MediaPlayer.create(context, fallbackUri)?.apply {
                isLooping = (nivel == NivelAlerta.PELIGRO || nivel == NivelAlerta.CRITICO)
                start()
            }
        }
    }

    fun stopAlertas() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
        vibrator.cancel()
    }
}
