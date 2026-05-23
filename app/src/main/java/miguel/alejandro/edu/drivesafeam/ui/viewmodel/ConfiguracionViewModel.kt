package miguel.alejandro.edu.drivesafeam.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import miguel.alejandro.edu.drivesafeam.data.model.ConfiguracionAlerta
import miguel.alejandro.edu.drivesafeam.data.model.ContactoEmergencia
import miguel.alejandro.edu.drivesafeam.data.model.TipoAccionEmergencia
import miguel.alejandro.edu.drivesafeam.data.repository.AuthRepository
import miguel.alejandro.edu.drivesafeam.data.repository.ConfiguracionRepository
import miguel.alejandro.edu.drivesafeam.data.repository.UsuarioRepository

class ConfiguracionViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val configuracionRepository = ConfiguracionRepository()
    private val usuarioRepository = UsuarioRepository()

    private val _configuracion = MutableStateFlow(ConfiguracionAlerta())
    val configuracion: StateFlow<ConfiguracionAlerta> = _configuracion

    private val _contactoEmergencia = MutableStateFlow<ContactoEmergencia?>(null)
    val contactoEmergencia: StateFlow<ContactoEmergencia?> = _contactoEmergencia

    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut: StateFlow<Boolean> = _isLoggedOut

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId()
            if (userId != null) {
                val configResult = configuracionRepository.getConfiguracion(userId)
                if (configResult.isSuccess && configResult.getOrNull() != null) {
                    _configuracion.value = configResult.getOrNull()!!
                }
                val userResult = usuarioRepository.getUsuario(userId)
                if (userResult.isSuccess) {
                    _contactoEmergencia.value = userResult.getOrNull()?.contactoEmergencia
                }
            }
        }
    }

    fun updateConfiguracion(newConfig: ConfiguracionAlerta) {
        _configuracion.value = newConfig
        saveToFirebase()
    }

    /** Actualiza únicamente la acción de emergencia (exclusión mutua). */
    fun updateAccionEmergencia(accion: TipoAccionEmergencia) {
        _configuracion.value = _configuracion.value.copy(accionEmergencia = accion)
        saveToFirebase()
    }

    fun updateNombreUsuario(nombre: String) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId()
            if (userId != null) {
                try {
                    val data = mapOf("nombre" to nombre)
                    com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(userId)
                        .set(data, com.google.firebase.firestore.SetOptions.merge()).await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun saveToFirebase() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId()
            if (userId != null) {
                configuracionRepository.saveConfiguracion(userId, _configuracion.value)
                val currentContacto = _contactoEmergencia.value
                if (currentContacto != null) {
                    try {
                        val data = mapOf("contactoEmergencia" to currentContacto)
                        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(userId)
                            .set(data, com.google.firebase.firestore.SetOptions.merge()).await()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun updateContacto(newContacto: ContactoEmergencia) {
        _contactoEmergencia.value = newContacto
        saveToFirebase()
    }

    fun logout() {
        authRepository.logout()
        _isLoggedOut.value = true
    }
}
