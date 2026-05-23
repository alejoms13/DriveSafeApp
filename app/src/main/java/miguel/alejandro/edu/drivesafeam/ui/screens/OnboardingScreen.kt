package miguel.alejandro.edu.drivesafeam.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeButton
import miguel.alejandro.edu.drivesafeam.ui.theme.*

@Composable
fun OnboardingScreen(
    onNavigateToPermisos: () -> Unit
) {
    val slides = listOf(
        OnboardingSlide(
            "Monitoreo Inteligente",
            "DriveSafe utiliza inteligencia artificial para analizar tu nivel de atención mientras conduces, protegiendo tu viaje en todo momento.",
            Icons.Default.Face
        ),
        OnboardingSlide(
            "Alertas en Tiempo Real",
            "Recibe advertencias visuales, sonoras y hápticas instantáneas cuando apartes la mirada o cierres los ojos por demasiado tiempo.",
            Icons.Default.NotificationsActive
        ),
        OnboardingSlide(
            "Asistencia de Emergencia",
            "En situaciones críticas, DriveSafe puede contactar automáticamente a tu red de apoyo compartiendo tu ubicación.",
            Icons.Default.Phone
        )
    )

    var currentPage by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DriveSafeDark)
    ) {
        // Gradient ambient background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DriveSafeOrange.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Animated content for the text
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width } + fadeIn(tween(400))).togetherWith(slideOutHorizontally { width -> -width } + fadeOut(tween(400)))
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn(tween(400))).togetherWith(slideOutHorizontally { width -> width } + fadeOut(tween(400)))
                    }
                }
            ) { page ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(DriveSafeSurface2),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = slides[page].icon,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint = DriveSafeOrange
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = slides[page].title,
                        style = MaterialTheme.typography.displaySmall,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = slides[page].description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextGray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Pager Indicator
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(slides.size) { iteration ->
                    val color = if (currentPage == iteration) DriveSafeOrange else DriveSafeSurface3
                    val width = if (currentPage == iteration) 24.dp else 8.dp
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            // Next / Finish button
            DriveSafeButton(
                text = if (currentPage == slides.size - 1) "Comenzar" else "Siguiente",
                onClick = {
                    if (currentPage == slides.size - 1) {
                        onNavigateToPermisos()
                    } else {
                        currentPage++
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

data class OnboardingSlide(val title: String, val description: String, val icon: ImageVector)
