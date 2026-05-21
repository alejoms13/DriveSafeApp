package miguel.alejandro.edu.drivesafeam.ui.screens

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeButton

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Permisos Necesarios",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        when (currentStep) {
            0 -> {
                Text(
                    text = "DriveSafe requiere acceso a la cámara frontal para monitorear tu nivel de atención y somnolencia mientras conduces.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                DriveSafeButton(
                    text = "Permitir Cámara",
                    onClick = { cameraPermissionState.launchPermissionRequest() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            1 -> {
                Text(
                    text = "DriveSafe necesita enviarte notificaciones para mantener el servicio activo en segundo plano de forma segura.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                DriveSafeButton(
                    text = "Permitir Notificaciones",
                    onClick = { notificationPermissionState?.launchPermissionRequest() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            2 -> {
                Text(
                    text = "DriveSafe utiliza tu ubicación para rastrear tu ruta y poder compartirla en caso de emergencia.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                DriveSafeButton(
                    text = "Permitir Ubicación",
                    onClick = { locationPermissionState.launchPermissionRequest() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
