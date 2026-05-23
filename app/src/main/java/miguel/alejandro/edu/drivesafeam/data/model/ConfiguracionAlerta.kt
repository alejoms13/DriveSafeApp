package miguel.alejandro.edu.drivesafeam.data.model

/**
 * Modelo de configuración personalizada del conductor.
 *
 * CAMBIO ARQUITECTÓNICO v2.0:
 * Se reemplazaron los dos temporizadores independientes (secondsBeforeWhatsapp
 * y secondsBeforeCall) por un único campo [accionEmergencia] de tipo
 * [TipoAccionEmergencia]. El usuario selecciona UNA sola acción crítica.
 * Esto evita solapamientos de alertas y simplifica el flujo de emergencia.
 */
data class ConfiguracionAlerta(
    // ── Feedback sensorial ──────────────────────────────────────
    val sonidoActivo: Boolean = true,
    val vibracionActiva: Boolean = true,
    val flashActivo: Boolean = true,
    val notificacionesActivas: Boolean = true,
    val camaraActiva: Boolean = true,

    // ── Parámetros de detección ──────────────────────────────────
    val intensidadVibracion: Float = 1.0f,
    val volumenAlerta: Float = 1.0f,
    val sensibilidadDeteccion: Float = 0.5f,   // 0f a 1f
    val tiempoAlerta: Int = 3,                  // segundos para alerta de distracción
    val tiempoCritico: Int = 15,                // segundos acumulados para alerta crítica
    val monitoringIntervalMs: Long = 200L,      // intervalo entre lecturas de ML Kit (200ms = 5fps)

    // ── Acción de emergencia (exclusiva y mutuamente excluyente) ──
    val accionEmergencia: TipoAccionEmergencia = TipoAccionEmergencia.WHATSAPP,

    // ── Retraso antes de ejecutar la acción de emergencia ────────
    val secondsBeforeAction: Int = 15           // único timer, aplica a la acción seleccionada
)
