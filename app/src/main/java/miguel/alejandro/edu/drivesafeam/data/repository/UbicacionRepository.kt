package miguel.alejandro.edu.drivesafeam.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

class UbicacionRepository(context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(): Result<Location?> {
        return try {
            val location = fusedLocationClient.lastLocation.await()
            Result.success(location)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
