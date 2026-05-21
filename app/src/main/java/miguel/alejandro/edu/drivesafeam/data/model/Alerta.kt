package miguel.alejandro.edu.drivesafeam.data.model

data class Alerta(
    val uid: String = "",
    val tipo: String = "",       // "Distracción", "Somnolencia", "Rostro no encontrado"
    val nivel: String = "",      // "LEVE", "MEDIA", "CRÍTICA"
    val timestamp: Long = 0L,    // Milisegundos
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val duracion: Int = 0        // Duración en segundos
)
