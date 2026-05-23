package miguel.alejandro.edu.drivesafeam.ui.screens

import android.preference.PreferenceManager
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeButton
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeCard
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeIconButton
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeTextField
import miguel.alejandro.edu.drivesafeam.ui.theme.*
import miguel.alejandro.edu.drivesafeam.ui.viewmodel.MapaViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaScreen(
    onNavigateBack: () -> Unit,
    mapaViewModel: MapaViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentLocation by mapaViewModel.currentLocation.collectAsState()
    val puntosMapa by mapaViewModel.puntosMapa.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var descripcionText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        // Configuración para dark mode en mapas (usamos DarkMatter o invertimos colores en tiles si fuera posible, OSMdroid nativo es limitado pero podemos usar tiles neutros)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = DriveSafeSurface2,
            title = {
                Text(
                    text = "Añadir Marcador",
                    color = TextWhite,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
                    Text(
                        text = "Describe el incidente o punto de interés:",
                        color = TextGrayLight,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    DriveSafeTextField(
                        value = descripcionText,
                        onValueChange = { if (it.length <= 50) descripcionText = it },
                        placeholder = "Ej: Precaución, desvío",
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedLocation?.let { loc ->
                            mapaViewModel.agregarPuntoMapa(
                                latitude = loc.latitude,
                                longitude = loc.longitude,
                                descripcion = descripcionText,
                                context = context
                            )
                        }
                        showDialog = false
                        descripcionText = ""
                        selectedLocation = null
                    },
                    enabled = descripcionText.isNotBlank()
                ) {
                    Text("Guardar", color = DriveSafeOrange, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar", color = TextGray)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DriveSafeDark)
            .systemBarsPadding()
    ) {
        // ── Top Bar Custom ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DriveSafeIconButton(
                icon = Icons.Default.ArrowBack,
                onClick = onNavigateBack,
                contentDescription = "Regresar"
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Mapa de Alertas",
                style = MaterialTheme.typography.displaySmall,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
        }

        // ── Contenedor del Mapa ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(DriveSafeSurface0)
        ) {
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(16.0)
                        
                        // Overlay para taps
                        val mapEventsReceiver = object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                                if (p != null) {
                                    selectedLocation = p
                                    descripcionText = ""
                                    showDialog = true
                                    return true
                                }
                                return false
                            }

                            override fun longPressHelper(p: GeoPoint?): Boolean {
                                if (p != null) {
                                    selectedLocation = p
                                    descripcionText = ""
                                    showDialog = true
                                    return true
                                }
                                return false
                            }
                        }
                        overlays.add(0, MapEventsOverlay(mapEventsReceiver))
                    }
                },
                update = { mapView ->
                    val markers = mapView.overlays.filterIsInstance<Marker>()
                    mapView.overlays.removeAll(markers)

                    // Marcador del usuario (Naranja Premium)
                    currentLocation?.let { location ->
                        val geoPoint = GeoPoint(location.latitude, location.longitude)
                        mapView.controller.setCenter(geoPoint)
                        
                        val marker = Marker(mapView).apply {
                            position = geoPoint
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            title = "Tú"
                        }
                        
                        val sizePx = (48 * context.resources.displayMetrics.density).toInt()
                        val bmpUser = android.graphics.Bitmap.createBitmap(sizePx, sizePx, android.graphics.Bitmap.Config.ARGB_8888)
                        val canvasUser = android.graphics.Canvas(bmpUser)
                        val paintUser = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
                        
                        // Glow exterior
                        paintUser.color = android.graphics.Color.parseColor("#33FF6D00")
                        canvasUser.drawCircle(sizePx / 2f, sizePx / 2f, sizePx / 2f, paintUser)
                        // Círculo central
                        paintUser.color = android.graphics.Color.parseColor("#FF6D00")
                        canvasUser.drawCircle(sizePx / 2f, sizePx / 2f, sizePx / 4f, paintUser)
                        // Borde blanco interior
                        paintUser.color = android.graphics.Color.WHITE
                        canvasUser.drawCircle(sizePx / 2f, sizePx / 2f, sizePx / 8f, paintUser)
                        
                        marker.icon = android.graphics.drawable.BitmapDrawable(context.resources, bmpUser)
                        mapView.overlays.add(marker)
                    }

                    // Marcadores comunitarios
                    puntosMapa.forEach { punto ->
                        val geoPoint = GeoPoint(punto.latitude, punto.longitude)
                        val marker = Marker(mapView).apply {
                            position = geoPoint
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = punto.descripcion
                            snippet = "Alerta Comunitaria"
                        }
                        
                        // Ícono pin rojo/oscuro
                        val sizePx = (40 * context.resources.displayMetrics.density).toInt()
                        val bmpReport = android.graphics.Bitmap.createBitmap(sizePx, sizePx, android.graphics.Bitmap.Config.ARGB_8888)
                        val canvasReport = android.graphics.Canvas(bmpReport)
                        val paintReport = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
                        paintReport.color = android.graphics.Color.parseColor("#FF1744")
                        
                        val path = android.graphics.Path()
                        path.moveTo(sizePx / 2f, sizePx * 0.1f)
                        path.lineTo(sizePx * 0.9f, sizePx * 0.7f)
                        path.lineTo(sizePx * 0.1f, sizePx * 0.7f)
                        path.close()
                        canvasReport.drawPath(path, paintReport)
                        
                        paintReport.color = android.graphics.Color.WHITE
                        paintReport.textSize = sizePx * 0.4f
                        paintReport.textAlign = android.graphics.Paint.Align.CENTER
                        paintReport.typeface = android.graphics.Typeface.DEFAULT_BOLD
                        canvasReport.drawText("!", sizePx / 2f, sizePx * 0.6f, paintReport)
                        
                        marker.icon = android.graphics.drawable.BitmapDrawable(context.resources, bmpReport)
                        mapView.overlays.add(marker)
                    }

                    mapView.invalidate()
                },
                modifier = Modifier.fillMaxSize()
            )
            
            // ── Floating Action Button superpuesto ──
            FloatingActionButton(
                onClick = {
                    currentLocation?.let { loc ->
                        selectedLocation = GeoPoint(loc.latitude, loc.longitude)
                        descripcionText = ""
                        showDialog = true
                    } ?: run {
                        android.widget.Toast.makeText(context, "Buscando ubicación...", android.widget.Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = DriveSafeOrange,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Reporte")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
