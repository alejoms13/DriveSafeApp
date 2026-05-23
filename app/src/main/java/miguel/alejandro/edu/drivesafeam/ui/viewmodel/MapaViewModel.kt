package miguel.alejandro.edu.drivesafeam.ui.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import miguel.alejandro.edu.drivesafeam.data.model.PuntoMapa
import miguel.alejandro.edu.drivesafeam.data.repository.PuntosMapaRepository
import miguel.alejandro.edu.drivesafeam.data.repository.UbicacionRepository
import com.google.firebase.auth.FirebaseAuth

class MapaViewModel(application: Application) : AndroidViewModel(application) {
    private val ubicacionRepository = UbicacionRepository(application)
    private val puntosMapaRepository = PuntosMapaRepository()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation

    private val _puntosMapa = MutableStateFlow<List<PuntoMapa>>(emptyList())
    val puntosMapa: StateFlow<List<PuntoMapa>> = _puntosMapa

    init {
        fetchLocation()
        observarPuntosMapa()
    }

    private fun fetchLocation() {
        viewModelScope.launch {
            val result = ubicacionRepository.getLastLocation()
            if (result.isSuccess) {
                _currentLocation.value = result.getOrNull()
            }
        }
    }

    private fun observarPuntosMapa() {
        viewModelScope.launch {
            try {
                puntosMapaRepository.obtenerPuntosRecientes(3).collect { puntos ->
                    _puntosMapa.value = puntos
                }
            } catch (e: Exception) {
                android.util.Log.e("MapaViewModel", "Error observando puntos", e)
            }
        }
    }

    fun agregarPuntoMapa(latitude: Double, longitude: Double, descripcion: String, context: android.content.Context) {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"
            val punto = PuntoMapa(
                userId = userId,
                latitude = latitude,
                longitude = longitude,
                descripcion = descripcion
            )
            val result = puntosMapaRepository.agregarPunto(punto)
            if (result.isSuccess) {
                android.widget.Toast.makeText(context, "Reporte agregado", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                android.widget.Toast.makeText(context, "Error agregando reporte", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
}
