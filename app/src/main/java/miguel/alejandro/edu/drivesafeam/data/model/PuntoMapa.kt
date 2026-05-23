package miguel.alejandro.edu.drivesafeam.data.model

data class PuntoMapa(
    val id: String = "",
    val userId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val descripcion: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
