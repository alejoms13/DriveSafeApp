package miguel.alejandro.edu.drivesafeam.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun login(correo: String, contrasena: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(correo, contrasena).await()
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(correo: String, contrasena: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(correo, contrasena).await()
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }
}
