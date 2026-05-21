package miguel.alejandro.edu.drivesafeam.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import miguel.alejandro.edu.drivesafeam.data.model.Usuario

class UsuarioRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("usuarios")

    suspend fun getUsuario(uid: String): Result<Usuario?> {
        return try {
            val snapshot = collection.document(uid).get().await()
            val usuario = snapshot.toObject(Usuario::class.java)
            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveUsuario(usuario: Usuario): Result<Unit> {
        return try {
            collection.document(usuario.uid).set(usuario).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
