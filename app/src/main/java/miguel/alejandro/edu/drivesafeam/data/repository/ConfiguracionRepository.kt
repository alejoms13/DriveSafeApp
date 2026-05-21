package miguel.alejandro.edu.drivesafeam.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import miguel.alejandro.edu.drivesafeam.data.model.ConfiguracionAlerta

class ConfiguracionRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("configuraciones")

    suspend fun getConfiguracion(userId: String): Result<ConfiguracionAlerta?> {
        return try {
            val snapshot = collection.document(userId).get().await()
            val config = snapshot.toObject(ConfiguracionAlerta::class.java)
            Result.success(config)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveConfiguracion(userId: String, config: ConfiguracionAlerta): Result<Unit> {
        return try {
            collection.document(userId).set(config).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
