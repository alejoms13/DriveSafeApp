package miguel.alejandro.edu.drivesafeam.data.model

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val correo: String = "",
    val contactoEmergencia: ContactoEmergencia? = null,
    val configuracion: ConfiguracionAlerta = ConfiguracionAlerta(),
    val onboardingVisto: Boolean = false
)
