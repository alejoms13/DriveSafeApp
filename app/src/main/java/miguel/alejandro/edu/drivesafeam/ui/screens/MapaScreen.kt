package miguel.alejandro.edu.drivesafeam.ui.screens

import android.preference.PreferenceManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import miguel.alejandro.edu.drivesafeam.ui.viewmodel.MapaViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaScreen(
    onNavigateBack: () -> Unit,
    mapaViewModel: MapaViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentLocation by mapaViewModel.currentLocation.collectAsState()

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)
                    }
                },
                update = { mapView ->
                    currentLocation?.let { location ->
                        val geoPoint = GeoPoint(location.latitude, location.longitude)
                        mapView.controller.setCenter(geoPoint)
                        
                        // Limpiar marcadores anteriores
                        mapView.overlays.clear()
                        
                        val marker = Marker(mapView)
                        marker.position = geoPoint
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.title = "Tu ubicación actual"
                        mapView.overlays.add(marker)
                        mapView.invalidate()
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
