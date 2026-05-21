package miguel.alejandro.edu.drivesafeam.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "drivesafe_prefs")

class PreferenciasManager(private val context: Context) {

    companion object {
        val ONBOARDING_COMPLETADO = booleanPreferencesKey("onboarding_completado")
        val ID_USUARIO = stringPreferencesKey("id_usuario")
    }

    val onboardingCompletado: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[ONBOARDING_COMPLETADO] ?: false
    }

    suspend fun guardarOnboardingCompletado(completado: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETADO] = completado
        }
    }

    val idUsuario: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[ID_USUARIO]
    }

    suspend fun guardarIdUsuario(id: String) {
        context.dataStore.edit { prefs ->
            prefs[ID_USUARIO] = id
        }
    }

    suspend fun limpiarSesion() {
        context.dataStore.edit { prefs ->
            prefs.remove(ID_USUARIO)
        }
    }
}
