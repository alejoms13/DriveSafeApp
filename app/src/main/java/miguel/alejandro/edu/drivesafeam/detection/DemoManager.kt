package miguel.alejandro.edu.drivesafeam.detection

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import miguel.alejandro.edu.drivesafeam.data.model.EstadoDeteccion

class DemoManager {
    fun startDemoSimulation(): Flow<EstadoDeteccion> = flow {
        // Simulamos un viaje normal por 5 segundos
        emit(EstadoDeteccion.Seguro)
        delay(5000)
        
        // Simulamos que desvía la mirada
        emit(EstadoDeteccion.Advertencia)
        delay(4000)
        
        // Vuelve a estar seguro
        emit(EstadoDeteccion.Seguro)
        delay(3000)
        
        // Simulamos somnolencia
        emit(EstadoDeteccion.Peligro)
        delay(3000)
        
        // Estado Crítico
        emit(EstadoDeteccion.Critico)
        delay(3000)
        
        // Vuelve a estar seguro
        emit(EstadoDeteccion.Seguro)
    }
}
