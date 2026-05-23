package miguel.alejandro.edu.drivesafeam.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import miguel.alejandro.edu.drivesafeam.data.model.PuntoMapa

class PuntosMapaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("reportes")

    suspend fun agregarPunto(punto: PuntoMapa): Result<Unit> {
        return try {
            val documentRef = if (punto.id.isEmpty()) collection.document() else collection.document(punto.id)
            val puntoConId = punto.copy(id = documentRef.id)
            documentRef.set(puntoConId).await()
            android.util.Log.d("PuntosMapa", "Punto agregado con exito: ${puntoConId.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("PuntosMapa", "Error agregando punto al mapa", e)
            Result.failure(e)
        }
    }

    fun obtenerPuntosRecientes(tiempoExpiracionHoras: Int = 3): Flow<List<PuntoMapa>> = callbackFlow {
        val tiempoLimite = System.currentTimeMillis() - (tiempoExpiracionHoras * 60 * 60 * 1000L)
        
        val listenerRegistration = collection
            .whereGreaterThan("timestamp", tiempoLimite)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("PuntosMapa", "Error obteniendo puntos", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    try {
                        val puntos = snapshot.documents.mapNotNull { it.toObject(PuntoMapa::class.java) }
                        trySend(puntos)
                    } catch (e: Exception) {
                        android.util.Log.e("PuntosMapa", "Error parseando puntos", e)
                    }
                }
            }

        awaitClose { listenerRegistration.remove() }
    }
}
