package miguel.alejandro.edu.drivesafeam

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.FirebaseApp
import org.osmdroid.config.Configuration

class DriveSafeApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializar configuración de OSMDroid (Obligatorio para evitar baneos de peticiones)
        Configuration.getInstance().userAgentValue = packageName

        // Inicializar Firebase
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Crear canal de notificaciones para el Foreground Service
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Monitoreo de Seguridad",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de alerta y monitoreo en tiempo real de DriveSafe"
            }
            
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "drivesafe_monitoreo_channel"
    }
}
