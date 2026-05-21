package miguel.alejandro.edu.drivesafeam.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import miguel.alejandro.edu.drivesafeam.data.local.PreferenciasManager

class OnboardingViewModel(private val preferenciasManager: PreferenciasManager) : ViewModel() {

    fun completarOnboarding() {
        viewModelScope.launch {
            preferenciasManager.guardarOnboardingCompletado(true)
        }
    }
}
