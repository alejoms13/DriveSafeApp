package miguel.alejandro.edu.drivesafeam.ui.screens

import android.Manifest
import android.os.Build
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeButton
import miguel.alejandro.edu.drivesafeam.ui.theme.*
import miguel.alejandro.edu.drivesafeam.ui.components.glow

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermisosScreen(
    onPermisosConcedidos: () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    
    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else null

    var currentStep by remember { mutableStateOf(0) }

    LaunchedEffect(cameraPermissionState.status, locationPermissionState.status, notificationPermissionState?.status) {
        if (cameraPermissionState.status.isGranted) {
            if (notificationPermissionState == null || notificationPermissionState.status.isGranted) {
                if (locationPermissionState.status.isGranted) {
                    onPermisosConcedidos()
                } else {
                    currentStep = 2
                }
            } else {
                currentStep = 1
            }
        } else {
            currentStep = 0
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DriveSafeDark)
    ) {
        // Ambient glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
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
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Configuración",
                style = MaterialTheme.typography.displayMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Necesitamos algunos permisos para protegerte",
                style = MaterialTheme.typography.bodyLarge,
                color = TextGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(slideOutHorizontally { width -> -width } + fadeOut())
                }
            ) { step ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val (icon, title, desc, btnText, onClick) = when (step) {
                        0 -> listOf(
                            Icons.Outlined.CameraAlt,
                            "Cámara Frontal",
                            "Utilizamos la cámara para analizar tu rostro y detectar signos de somnolencia o distracción al volante.",
                            "Permitir Cámara",
                            { cameraPermissionState.launchPermissionRequest() }
                        )
                        1 -> listOf(
                            Icons.Outlined.Notifications,
                            "Notificaciones",
                            "Mantendremos el servicio activo en segundo plano y te alertaremos visualmente cuando haya un problema.",
                            "Permitir Notificaciones",
                            { notificationPermissionState?.launchPermissionRequest() }
                        )
                        else -> listOf(
                            Icons.Outlined.LocationOn,
                            "Ubicación",
                            "Registramos la ubicación de las alertas para tu historial y envío automático a contactos de emergencia.",
                            "Permitir Ubicación",
                            { locationPermissionState.launchPermissionRequest() }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .glow(DriveSafeOrange.copy(alpha = 0.2f), radius = 16.dp, shapeRadius = 50.dp)
                            .clip(CircleShape)
                            .background(DriveSafeSurface1),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon as androidx.compose.ui.graphics.vector.ImageVector,
                            contentDescription = null,
                            tint = DriveSafeOrange,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = title as String,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = desc as String,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    DriveSafeButton(
                        text = btnText as String,
                        onClick = onClick as () -> Unit
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
            
            // Progress dots
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(3) { index ->
                    val color = if (index == currentStep) DriveSafeOrange else DriveSafeSurface3
                    val width = if (index == currentStep) 24.dp else 8.dp
                    
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
        }
    }
}
