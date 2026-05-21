package miguel.alejandro.edu.drivesafeam.data.model

data class ConfiguracionAlerta(
    val sonidoActivo: Boolean = true,
    val vibracionActiva: Boolean = true,
    val flashActivo: Boolean = true,
    val notificacionesActivas: Boolean = true,
    val camaraActiva: Boolean = true,
    val intensidadVibracion: Float = 0.5f,
    val volumenAlerta: Float = 0.8f,
    val sensibilidadDeteccion: Float = 0.5f, // 0f a 1f
    val tiempoAlerta: Int = 3,                // segundos para alerta de distracción
    val tiempoCritico: Int = 15               // segundos acumulados para alerta crítica
)
