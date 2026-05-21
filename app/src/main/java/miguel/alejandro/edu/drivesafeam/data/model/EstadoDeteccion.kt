package miguel.alejandro.edu.drivesafeam.data.model

sealed class EstadoDeteccion {
    object Seguro : EstadoDeteccion()
    object Advertencia : EstadoDeteccion()
    object Peligro : EstadoDeteccion()
    object Critico : EstadoDeteccion()
    object SinRostro : EstadoDeteccion()
    
    data class Confianza(
        val iluminacionInsuficiente: Boolean = false,
        val rostroParcial: Boolean = false,
        val camaraObstruida: Boolean = false,
        val gafasDeSolDetectadas: Boolean = false
    ) : EstadoDeteccion()
}
