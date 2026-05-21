package miguel.alejandro.edu.drivesafeam.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import miguel.alejandro.edu.drivesafeam.data.model.Alerta
import miguel.alejandro.edu.drivesafeam.data.repository.AlertaRepository
import miguel.alejandro.edu.drivesafeam.data.repository.AuthRepository

class HistorialViewModel : ViewModel() {
    private val alertaRepository = AlertaRepository()
    private val authRepository = AuthRepository()

    private val _alertas = MutableStateFlow<List<Alerta>>(emptyList())
    val alertas: StateFlow<List<Alerta>> = _alertas

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        cargarAlertas()
    }

    private fun cargarAlertas() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = authRepository.getCurrentUserId()
            if (userId != null) {
                val result = alertaRepository.getAlertasPorUsuario(userId)
                if (result.isSuccess) {
                    _alertas.value = result.getOrDefault(emptyList()).sortedByDescending { it.timestamp }
                }
            }
            _isLoading.value = false
        }
    }
}
