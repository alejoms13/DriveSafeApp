package miguel.alejandro.edu.drivesafeam.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import miguel.alejandro.edu.drivesafeam.data.repository.AuthRepository

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(correo: String, contrasena: String) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _authState.value = AuthState.Error("Por favor ingresa correo y contraseña")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.login(correo, contrasena)
            if (result.isSuccess) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Error de autenticación")
            }
        }
    }

    fun register(correo: String, contrasena: String, confirmar: String) {
        if (correo.isBlank() || contrasena.isBlank() || confirmar.isBlank()) {
            _authState.value = AuthState.Error("Todos los campos son obligatorios")
            return
        }
        
        if (contrasena != confirmar) {
            _authState.value = AuthState.Error("Las contraseñas no coinciden")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = authRepository.register(correo, contrasena)
            if (result.isSuccess) {
                _authState.value = AuthState.Success
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Error al registrar")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
