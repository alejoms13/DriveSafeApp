package miguel.alejandro.edu.drivesafeam.navigation

sealed class DriveSafeRoute(val route: String) {
    object Splash : DriveSafeRoute("splash")
    object Login : DriveSafeRoute("login")
    object Register : DriveSafeRoute("register")
    object Onboarding : DriveSafeRoute("onboarding")
    object Permisos : DriveSafeRoute("permisos")
    object Monitoreo : DriveSafeRoute("monitoreo")
    object Historial : DriveSafeRoute("historial")
    object Configuracion : DriveSafeRoute("configuracion")
    object Mapa : DriveSafeRoute("mapa")
    object Emergencia : DriveSafeRoute("emergencia")
}
