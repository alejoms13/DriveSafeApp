package miguel.alejandro.edu.drivesafeam.ui.screens

import android.content.Intent
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.outlined.VideocamOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import miguel.alejandro.edu.drivesafeam.data.model.EstadoDeteccion
import miguel.alejandro.edu.drivesafeam.detection.CameraManager
import miguel.alejandro.edu.drivesafeam.service.MonitoreoService
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeCard
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeIconButton
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeDisclaimer
import miguel.alejandro.edu.drivesafeam.ui.components.glow
import miguel.alejandro.edu.drivesafeam.ui.theme.*
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
    val isFlashActive by monitoreoViewModel.isFlashActive.collectAsState()
    var isMonitoring by remember { mutableStateOf(false) }

    val cameraManager = remember { CameraManager(context) }

    val prefManager = remember {
        miguel.alejandro.edu.drivesafeam.data.local.PreferenciasManager(context)
    }
    val nombreUsuario by prefManager.nombreUsuario.collectAsState(initial = "")

    // ── Flash control ──
    LaunchedEffect(isFlashActive) {
        if (isMonitoring) {
            cameraManager.setTorch(isFlashActive)
        }
    }

    // ── Cleanup ──
    DisposableEffect(Unit) {
        onDispose {
            cameraManager.shutdown()
            val stopIntent = Intent(context, MonitoreoService::class.java).apply {
                action = "STOP_SERVICE"
            }
            context.startService(stopIntent)
        }
    }

    DisposableEffect(isMonitoring) {
        if (isMonitoring) {
            val serviceIntent = Intent(context, MonitoreoService::class.java)
            context.startForegroundService(serviceIntent)
        }
        onDispose {
            if (!isMonitoring) {
                // Ya se maneja en el botón o en el onDispose de Unit
            }
        }
    }

    // ── Colores reactivos según estado ──
    val stateColor by animateColorAsState(
        targetValue = if (!isMonitoring) TextGray
        else when (estadoDeteccion) {
            is EstadoDeteccion.Seguro -> DriveSafeGreen
            is EstadoDeteccion.Advertencia -> DriveSafeYellow
            is EstadoDeteccion.Peligro, is EstadoDeteccion.Critico -> DriveSafeRed
            is EstadoDeteccion.SinRostro -> TextGray
            else -> TextGray
        },
        animationSpec = tween(400),
        label = "StateColor"
    )

    val stateText = if (!isMonitoring) {
        "Inactivo"
    } else when (estadoDeteccion) {
        is EstadoDeteccion.Seguro -> "Seguro"
        is EstadoDeteccion.Advertencia -> "Distracción"
        is EstadoDeteccion.Peligro -> "Somnolencia"
        is EstadoDeteccion.Critico -> "¡ALERTA!"
        is EstadoDeteccion.SinRostro -> "Buscando..."
        else -> "Iniciando"
    }

    val stateSubtext = if (!isMonitoring) {
        "Toca para iniciar monitoreo"
    } else when (estadoDeteccion) {
        is EstadoDeteccion.Seguro -> "Todo en orden"
        is EstadoDeteccion.Advertencia -> "¡Mira al frente!"
        is EstadoDeteccion.Peligro -> "¡Detente y descansa!"
        is EstadoDeteccion.Critico -> "¡Peligro inminente!"
        is EstadoDeteccion.SinRostro -> "Posiciónate frente a la cámara"
        else -> "Preparando sistemas..."
    }

    // ── Permission Launcher ──
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            isMonitoring = true
        } else {
            android.widget.Toast.makeText(
                context,
                "Se requieren permisos para monitorear",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    // ══════════════════════════════════════════════════════════
    //  LAYOUT PRINCIPAL
    // ══════════════════════════════════════════════════════════

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DriveSafeDark)
    ) {
        // ── Glow ambiental según estado activo ──
        if (isMonitoring) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                stateColor.copy(alpha = 0.08f),
                                stateColor.copy(alpha = 0.03f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // ═══════════════════════════════════════════════════
            //  TOP BAR — Saludo + Acciones
            // ═══════════════════════════════════════════════════
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (nombreUsuario.isNotEmpty()) "Hola, $nombreUsuario" else "Monitoreo",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isMonitoring) DriveSafeGreen else TextGrayDark)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isMonitoring) "Monitoreo activo" else "Monitoreo inactivo",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isMonitoring) DriveSafeGreen else TextGrayDark
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DriveSafeIconButton(
                        icon = Icons.Outlined.Map,
                        onClick = onNavigateToMapa,
                        contentDescription = "Mapa"
                    )
                    DriveSafeIconButton(
                        icon = Icons.Outlined.History,
                        onClick = onNavigateToHistorial,
                        contentDescription = "Historial"
                    )
                    DriveSafeIconButton(
                        icon = Icons.Outlined.Settings,
                        onClick = onNavigateToConfiguracion,
                        contentDescription = "Ajustes"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ═══════════════════════════════════════════════════
            //  HERO — Botón de Power Central
            // ═══════════════════════════════════════════════════
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                PowerButton(
                    isActive = isMonitoring,
                    stateColor = stateColor,
                    stateText = stateText,
                    stateSubtext = stateSubtext,
                    onToggle = {
                        if (isMonitoring) {
                            // DETENER
                            isMonitoring = false
                            cameraManager.stopCamera()
                            val stopIntent = Intent(context, MonitoreoService::class.java).apply {
                                action = "STOP_SERVICE"
                            }
                            context.startService(stopIntent)
                            monitoreoViewModel.resetearEstado()
                        } else {
                            // INICIAR (verificar permisos)
                            val hasCamera =
                                androidx.core.content.ContextCompat.checkSelfPermission(
                                    context,
                                    android.Manifest.permission.CAMERA
                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                            val hasLocation =
                                androidx.core.content.ContextCompat.checkSelfPermission(
                                    context,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION
                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                            var hasNotifications = true
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                hasNotifications =
                                    androidx.core.content.ContextCompat.checkSelfPermission(
                                        context,
                                        android.Manifest.permission.POST_NOTIFICATIONS
                                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                            }

                            if (hasCamera && hasLocation && hasNotifications) {
                                isMonitoring = true
                            } else {
                                val permissionsToRequest = mutableListOf(
                                    android.Manifest.permission.CAMERA,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION
                                )
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                    permissionsToRequest.add(android.Manifest.permission.POST_NOTIFICATIONS)
                                }
                                permissionLauncher.launch(permissionsToRequest.toTypedArray())
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ═══════════════════════════════════════════════════
            //  CAMERA PREVIEW — Vista de Cámara
            // ═══════════════════════════════════════════════════
            DriveSafeCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                surfaceColor = DriveSafeSurface0,
                borderColor = if (isMonitoring) stateColor.copy(alpha = 0.2f) else BorderSubtle
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    if (isMonitoring) {
                        AndroidView(
                            factory = { ctx ->
                                val previewView = PreviewView(ctx)
                                cameraManager.startCamera(lifecycleOwner, previewView) { faces ->
                                    monitoreoViewModel.procesarRostros(faces)
                                }
                                previewView
                            },
                            onRelease = {
                                cameraManager.stopCamera()
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // ── Indicador REC ──
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                        ) {
                            RecIndicator()
                        }
                    } else {
                        // ── Estado Inactivo de Cámara ──
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.VideocamOff,
                                contentDescription = "Cámara inactiva",
                                tint = TextGrayDark,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Cámara en espera",
                                color = TextGrayDark,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Activa el monitoreo para comenzar",
                                color = TextGrayDark.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ═══════════════════════════════════════════════════
            //  DISCLAIMER
            // ═══════════════════════════════════════════════════
            DriveSafeDisclaimer(
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ═══════════════════════════════════════════════════════════════
//  POWER BUTTON — Botón Central de Encendido Premium
// ═══════════════════════════════════════════════════════════════

@Composable
private fun PowerButton(
    isActive: Boolean,
    stateColor: Color,
    stateText: String,
    stateSubtext: String,
    onToggle: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "PowerPulse")

    // ── Pulso del anillo exterior ──
    val ringScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isActive) 1.15f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isActive) 1500 else 3000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "RingScale"
    )

    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = if (isActive) 0.4f else 0.15f,
        targetValue = if (isActive) 0.15f else 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isActive) 1500 else 3000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "RingAlpha"
    )

    // ── Rotación del arco de progreso (sólo activo) ──
    val arcRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        ),
        label = "ArcRotation"
    )

    // ── Animación del botón al presionar ──
    val buttonScale by animateFloatAsState(
        targetValue = if (isActive) 1f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "ButtonScale"
    )

    val buttonColor by animateColorAsState(
        targetValue = if (isActive) stateColor else DriveSafeOrange,
        animationSpec = tween(400),
        label = "ButtonColor"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(180.dp),
            contentAlignment = Alignment.Center
        ) {
            // ── Anillo exterior pulsante ──
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(ringScale)
                    .clip(CircleShape)
                    .background(buttonColor.copy(alpha = ringAlpha * 0.3f))
            )

            // ── Segundo anillo ──
            Box(
                modifier = Modifier
                    .size(155.dp)
                    .clip(CircleShape)
                    .background(buttonColor.copy(alpha = 0.06f))
            )

            // ── Arco rotatorio (sólo activo) ──
            if (isActive) {
                Canvas(
                    modifier = Modifier
                        .size(155.dp)
                        .graphicsLayer { rotationZ = arcRotation }
                ) {
                    drawArc(
                        color = buttonColor.copy(alpha = 0.5f),
                        startAngle = 0f,
                        sweepAngle = 120f,
                        useCenter = false,
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            // ── Botón central clickeable ──
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(buttonScale)
                    .drawBehind {
                        drawIntoCanvas { canvas ->
                            val paint = Paint()
                            val frameworkPaint = paint.asFrameworkPaint()
                            frameworkPaint.color = Color.Transparent.toArgb()
                            frameworkPaint.setShadowLayer(
                                24.dp.toPx(),
                                0f,
                                0f,
                                buttonColor
                                    .copy(alpha = 0.5f)
                                    .toArgb()
                            )
                            canvas.drawCircle(
                                Offset(size.width / 2f, size.height / 2f),
                                size.minDimension / 2f,
                                paint
                            )
                        }
                    }
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                buttonColor.copy(alpha = 0.25f),
                                buttonColor.copy(alpha = 0.10f),
                                DriveSafeSurface1
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                buttonColor.copy(alpha = 0.6f),
                                buttonColor.copy(alpha = 0.1f),
                                buttonColor.copy(alpha = 0.4f),
                                buttonColor.copy(alpha = 0.1f),
                                buttonColor.copy(alpha = 0.6f)
                            )
                        ),
                        shape = CircleShape
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isActive) Icons.Outlined.Shield else Icons.Default.PlayArrow,
                    contentDescription = if (isActive) "Detener monitoreo" else "Iniciar monitoreo",
                    tint = buttonColor,
                    modifier = Modifier.size(if (isActive) 40.dp else 48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Estado textual ──
        Text(
            text = stateText,
            style = MaterialTheme.typography.titleMedium,
            color = stateColor,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stateSubtext,
            style = MaterialTheme.typography.bodySmall,
            color = TextGray,
            textAlign = TextAlign.Center
        )
    }
}

// ═══════════════════════════════════════════════════════════════
//  REC INDICATOR — Indicador de Grabación
// ═══════════════════════════════════════════════════════════════

@Composable
private fun RecIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "RecPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "RecAlpha"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DriveSafeRed.copy(alpha = 0.2f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(DriveSafeRed.copy(alpha = alpha))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "REC",
            color = DriveSafeRed,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}
