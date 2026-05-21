package miguel.alejandro.edu.drivesafeam.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import miguel.alejandro.edu.drivesafeam.data.model.Alerta

class AlertaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("alertas")

    suspend fun saveAlerta(alerta: Alerta): Result<Unit> {
        return try {
            val document = if (alerta.uid.isEmpty()) collection.document() else collection.document(alerta.uid)
            val alertaConId = if (alerta.uid.isEmpty()) alerta.copy(uid = document.id) else alerta
            document.set(alertaConId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAlertasPorUsuario(userId: String): Result<List<Alerta>> {
        return try {
            val snapshot = collection.whereEqualTo("usuarioId", userId)
                .get()
                .await()
            val alertas = snapshot.toObjects(Alerta::class.java)
            Result.success(alertas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
