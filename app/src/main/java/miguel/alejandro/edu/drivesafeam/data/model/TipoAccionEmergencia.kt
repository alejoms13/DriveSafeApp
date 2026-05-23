package miguel.alejandro.edu.drivesafeam.data.model

/**
 * Discriminador de acción crítica de emergencia.
 *
 * El usuario elige UNA sola acción que se ejecutará automáticamente
 * cuando el protocolo SOS se dispare. Esto elimina la ambigüedad de
 * tener dos temporizadores independientes que podían solaparse o
 * interferir con la atención del conductor al volante.
 *
 * Persiste en Firestore como String en el campo "accionEmergencia"
 * dentro del documento de usuario.
 */
enum class TipoAccionEmergencia {
    WHATSAPP,   // Envía mensaje de WhatsApp formateado con ubicación GPS
    LLAMADA,    // Realiza una llamada telefónica directa al contacto
    NINGUNA     // No ejecuta ninguna acción automática (solo alertas locales)
}
