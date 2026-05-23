package miguel.alejandro.edu.drivesafeam.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import miguel.alejandro.edu.drivesafeam.R
import miguel.alejandro.edu.drivesafeam.data.local.PreferenciasManager
import miguel.alejandro.edu.drivesafeam.ui.theme.DriveSafeDark
import miguel.alejandro.edu.drivesafeam.ui.theme.DriveSafeGlow
import miguel.alejandro.edu.drivesafeam.ui.theme.DriveSafeOrange
import miguel.alejandro.edu.drivesafeam.ui.theme.TextWhite

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
    val nombreUsuario by prefManager.nombreUsuario.collectAsState(initial = "")

    var startAnimation by remember { mutableStateOf(false) }
    
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )
    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
    )

    LaunchedEffect(key1 = onboardingCompletado, key2 = idUsuario) {
        startAnimation = true
        delay(2200) // Mostrar el logo por 2.2 segundos para apreciar el glow
        
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
            .background(DriveSafeDark)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        // Fondo glow animado
        DriveSafeGlow(color = DriveSafeOrange, animated = true)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "DriveSafe Logo",
                modifier = Modifier.size(140.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            val greeting = if (!idUsuario.isNullOrEmpty() && nombreUsuario.isNotEmpty()) {
                "HOLA, ${nombreUsuario.uppercase()}"
            } else {
                "DRIVESAFE"
            }
            
            Text(
                text = greeting,
                color = TextWhite,
                style = MaterialTheme.typography.titleLarge,
                letterSpacing = 4.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}
