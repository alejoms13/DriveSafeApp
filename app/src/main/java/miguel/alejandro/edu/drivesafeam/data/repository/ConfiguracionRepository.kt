package miguel.alejandro.edu.drivesafeam.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import miguel.alejandro.edu.drivesafeam.data.model.ConfiguracionAlerta
import miguel.alejandro.edu.drivesafeam.data.model.Usuario

class ConfiguracionRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("users")

    suspend fun getConfiguracion(userId: String): Result<ConfiguracionAlerta?> {
        return try {
            val snapshot = collection.document(userId).get().await()
            val config = snapshot.toObject(Usuario::class.java)?.configuracion
            Result.success(config)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveConfiguracion(userId: String, config: ConfiguracionAlerta): Result<Unit> {
        return try {
            val data = mapOf("configuracion" to config)
            collection.document(userId).set(data, com.google.firebase.firestore.SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
