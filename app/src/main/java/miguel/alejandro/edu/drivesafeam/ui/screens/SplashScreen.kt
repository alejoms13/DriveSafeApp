package miguel.alejandro.edu.drivesafeam.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import miguel.alejandro.edu.drivesafeam.R
import miguel.alejandro.edu.drivesafeam.data.local.PreferenciasManager
import miguel.alejandro.edu.drivesafeam.ui.theme.DriveSafeGlow

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToMain: () -> Unit
) {
    val context = LocalContext.current
    val prefManager = remember { PreferenciasManager(context) }
    
    val onboardingCompletado by prefManager.onboardingCompletado.collectAsState(initial = null)
    val idUsuario by prefManager.idUsuario.collectAsState(initial = null)

    var startAnimation by remember { mutableStateOf(false) }
    
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )
    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )

    LaunchedEffect(key1 = onboardingCompletado, key2 = idUsuario) {
        startAnimation = true
        delay(2000) // Mostrar el logo por 2 segundos
        
        // Evitamos navegar si aún no sabemos los estados (son null inicialmente)
        if (onboardingCompletado != null && idUsuario != null) {
            if (idUsuario?.isNotEmpty() == true) {
                onNavigateToMain()
            } else if (onboardingCompletado == true) {
                onNavigateToLogin()
            } else {
                onNavigateToOnboarding()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
        ) {
            DriveSafeGlow(color = MaterialTheme.colorScheme.primary)
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Asegurar que este drawable exista
                contentDescription = "DriveSafe Logo",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
