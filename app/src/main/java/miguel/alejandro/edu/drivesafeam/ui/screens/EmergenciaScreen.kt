package miguel.alejandro.edu.drivesafeam.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeButton
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeOutlineButton
import miguel.alejandro.edu.drivesafeam.ui.theme.*

@Composable
fun EmergenciaScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "EmergencyPulse")

    val bgScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BgScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DriveSafeDark)
    ) {
        // Red Pulsating Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(bgScale)
                .background(DriveSafeRed.copy(alpha = 0.15f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(DriveSafeRed.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(DriveSafeRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WarningAmber,
                        contentDescription = "Alerta",
                        tint = TextWhite,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "¡ALERTA CRÍTICA!",
                style = MaterialTheme.typography.displayMedium,
                color = DriveSafeRed,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Se ha detectado una somnolencia prolongada o distracción severa.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextWhiteMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            DriveSafeButton(
                text = "ESTOY BIEN",
                onClick = onNavigateBack
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            DriveSafeOutlineButton(
                text = "PEDIR AYUDA (WhatsApp)",
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    val message = "Alerta de DriveSafe: Necesito ayuda. Por favor contacta conmigo."
                    intent.data = Uri.parse("https://wa.me/?text=${Uri.encode(message)}")
                    context.startActivity(intent)
                }
            )
        }
    }
}
