package miguel.alejandro.edu.drivesafeam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import miguel.alejandro.edu.drivesafeam.navigation.DriveSafeRoute
import miguel.alejandro.edu.drivesafeam.ui.screens.LoginScreen
import miguel.alejandro.edu.drivesafeam.ui.screens.RegisterScreen
import miguel.alejandro.edu.drivesafeam.ui.screens.SplashScreen
import miguel.alejandro.edu.drivesafeam.ui.theme.DrivesafeamTheme
import miguel.alejandro.edu.drivesafeam.ui.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DrivesafeamTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = DriveSafeRoute.Splash.route
                    ) {
                        composable(DriveSafeRoute.Splash.route) {
                            SplashScreen(
                                onNavigateToLogin = {
                                    navController.navigate(DriveSafeRoute.Login.route) {
                                        popUpTo(DriveSafeRoute.Splash.route) { inclusive = true }
                                    }
                                },
                                onNavigateToOnboarding = {
                                    navController.navigate(DriveSafeRoute.Onboarding.route) {
                                        popUpTo(DriveSafeRoute.Splash.route) { inclusive = true }
                                    }
                                },
                                onNavigateToMain = {
                                    navController.navigate(DriveSafeRoute.Monitoreo.route) {
                                        popUpTo(DriveSafeRoute.Splash.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        
                        composable(DriveSafeRoute.Onboarding.route) {
                            val onboardingViewModel: miguel.alejandro.edu.drivesafeam.ui.viewmodel.OnboardingViewModel = viewModel(
                                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                        return miguel.alejandro.edu.drivesafeam.ui.viewmodel.OnboardingViewModel(miguel.alejandro.edu.drivesafeam.data.local.PreferenciasManager(applicationContext)) as T
                                    }
                                }
                            )
                            miguel.alejandro.edu.drivesafeam.ui.screens.OnboardingScreen(
                                onNavigateToPermisos = {
                                    navController.navigate(DriveSafeRoute.Permisos.route) {
                                        popUpTo(DriveSafeRoute.Onboarding.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(DriveSafeRoute.Permisos.route) {
                            val onboardingViewModel: miguel.alejandro.edu.drivesafeam.ui.viewmodel.OnboardingViewModel = viewModel(
                                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                        return miguel.alejandro.edu.drivesafeam.ui.viewmodel.OnboardingViewModel(miguel.alejandro.edu.drivesafeam.data.local.PreferenciasManager(applicationContext)) as T
                                    }
                                }
                            )
                            miguel.alejandro.edu.drivesafeam.ui.screens.PermisosScreen(
                                onPermisosConcedidos = {
                                    onboardingViewModel.completarOnboarding()
                                    navController.navigate(DriveSafeRoute.Login.route) {
                                        popUpTo(DriveSafeRoute.Permisos.route) { inclusive = true }
                                    }
                                }
                            )
                        }
                        
                        composable(DriveSafeRoute.Login.route) {
                            LoginScreen(
                                authViewModel = authViewModel,
                                onNavigateToRegister = {
                                    navController.navigate(DriveSafeRoute.Register.route)
                                },
                                onLoginSuccess = {
                                    navController.navigate(DriveSafeRoute.Monitoreo.route) {
                                        popUpTo(DriveSafeRoute.Login.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(DriveSafeRoute.Register.route) {
                            RegisterScreen(
                                authViewModel = authViewModel,
                                onNavigateToLogin = {
                                    navController.popBackStack()
                                },
                                onRegisterSuccess = {
                                    navController.navigate(DriveSafeRoute.Monitoreo.route) {
                                        popUpTo(DriveSafeRoute.Register.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(DriveSafeRoute.Monitoreo.route) {
                            miguel.alejandro.edu.drivesafeam.ui.screens.MonitoreoScreen(
                                onNavigateToHistorial = { navController.navigate(DriveSafeRoute.Historial.route) },
                                onNavigateToConfiguracion = { navController.navigate(DriveSafeRoute.Configuracion.route) },
                                onNavigateToMapa = { navController.navigate(DriveSafeRoute.Mapa.route) }
                            )
                        }

                        composable(DriveSafeRoute.Historial.route) {
                            miguel.alejandro.edu.drivesafeam.ui.screens.HistorialScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(DriveSafeRoute.Configuracion.route) {
                            miguel.alejandro.edu.drivesafeam.ui.screens.ConfiguracionScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToLogin = {
                                    navController.navigate(DriveSafeRoute.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(DriveSafeRoute.Emergencia.route) {
                            miguel.alejandro.edu.drivesafeam.ui.screens.EmergenciaScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(DriveSafeRoute.Mapa.route) {
                            miguel.alejandro.edu.drivesafeam.ui.screens.MapaScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
