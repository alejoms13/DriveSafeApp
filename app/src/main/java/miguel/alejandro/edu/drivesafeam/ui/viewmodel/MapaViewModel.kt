package miguel.alejandro.edu.drivesafeam.ui.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import miguel.alejandro.edu.drivesafeam.data.repository.UbicacionRepository

class MapaViewModel(application: Application) : AndroidViewModel(application) {
    private val ubicacionRepository = UbicacionRepository(application)

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation

    init {
        fetchLocation()
    }

    private fun fetchLocation() {
        viewModelScope.launch {
            val result = ubicacionRepository.getLastLocation()
            if (result.isSuccess) {
                _currentLocation.value = result.getOrNull()
            }
        }
    }
}
