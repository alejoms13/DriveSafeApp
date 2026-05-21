package miguel.alejandro.edu.drivesafeam.data.model

enum class TipoAlerta {
    NO_FACE,
    DROWSINESS,
    DISTRACTION
}

enum class NivelAlerta {
    ADVERTENCIA,
    PELIGRO,
    CRITICO
}

data class Alerta(
    val uid: String = "",
    val usuarioId: String = "",
    val tipo: TipoAlerta = TipoAlerta.DISTRACTION,
    val nivel: NivelAlerta = NivelAlerta.ADVERTENCIA,
    val timestamp: Long = System.currentTimeMillis(),
    val latitud: Double? = null,
    val longitud: Double? = null,
    val duracion: Int = 0        // Duración en segundos
)
