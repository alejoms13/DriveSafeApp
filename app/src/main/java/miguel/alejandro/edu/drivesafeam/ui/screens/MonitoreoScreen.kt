package miguel.alejandro.edu.drivesafeam.ui.screens

import android.content.Intent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import miguel.alejandro.edu.drivesafeam.data.model.EstadoDeteccion
import miguel.alejandro.edu.drivesafeam.detection.CameraManager
import miguel.alejandro.edu.drivesafeam.service.MonitoreoService
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeButton
import miguel.alejandro.edu.drivesafeam.ui.theme.DriveSafeGlow
import miguel.alejandro.edu.drivesafeam.ui.viewmodel.MonitoreoViewModel

@Composable
fun MonitoreoScreen(
    monitoreoViewModel: MonitoreoViewModel = viewModel(),
    onNavigateToHistorial: () -> Unit,
    onNavigateToConfiguracion: () -> Unit,
    onNavigateToMapa: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val estadoDeteccion by monitoreoViewModel.estadoDeteccion.collectAsState()
    
    val cameraManager = remember { CameraManager(context) }
    
    DisposableEffect(Unit) {
        val serviceIntent = Intent(context, MonitoreoService::class.java)
        context.startForegroundService(serviceIntent)
        
        onDispose {
            cameraManager.shutdown()
            val stopIntent = Intent(context, MonitoreoService::class.java).apply {
                action = "STOP_SERVICE"
            }
            context.startService(stopIntent)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Monitoreo",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Row {
                TextButton(onClick = onNavigateToMapa) { Text("Mapa") }
                TextButton(onClick = onNavigateToHistorial) { Text("Historial") }
                TextButton(onClick = onNavigateToConfiguracion) { Text("Ajustes") }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Estado Indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            contentAlignment = Alignment.Center
        ) {
            val color = when (estadoDeteccion) {
                is EstadoDeteccion.Seguro -> Color(0xFF4CAF50)
                is EstadoDeteccion.Advertencia -> Color(0xFFFFC107)
                is EstadoDeteccion.Peligro, is EstadoDeteccion.Critico -> Color(0xFFF44336)
                is EstadoDeteccion.SinRostro -> Color.Gray
                else -> Color.Gray
            }
            
            val texto = when (estadoDeteccion) {
                is EstadoDeteccion.Seguro -> "Seguro"
                is EstadoDeteccion.Advertencia -> "Distracción Detectada"
                is EstadoDeteccion.Peligro -> "Somnolencia Detectada"
                is EstadoDeteccion.Critico -> "¡ALERTA CRÍTICA!"
                is EstadoDeteccion.SinRostro -> "Buscando Rostro..."
                else -> "Iniciando..."
            }
            
            DriveSafeGlow(color = color)
            
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = texto,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Camera Preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
                .clip(MaterialTheme.shapes.large)
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    cameraManager.startCamera(lifecycleOwner, previewView) { faces ->
                        monitoreoViewModel.procesarRostros(faces)
                    }
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
