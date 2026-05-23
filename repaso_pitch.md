# 🚀 DriveSafe — Pitch Técnico Completo v2.0 (Post-Refactorización)

> Documento de arquitectura de ingeniería para sustentación, defensa técnica y presentación estratégica del proyecto MVP.
> Actualizado con las refactorizaciones del sprint final: AudioAlertManager, TipoAccionEmergencia, WhatsApp enriquecido y OnboardingScreen.

---

## 📋 Tabla de Contenidos

1. [El Problema](#1-el-problema)
2. [Stack Tecnológico Completo](#2-stack-tecnológico-completo)
3. [Arquitectura del Proyecto](#3-arquitectura-del-proyecto)
4. [Esquema Jerárquico de Firestore](#4-esquema-jerárquico-de-firestore)
5. [Algoritmo de Edge AI — ML Kit](#5-algoritmo-de-edge-ai--ml-kit)
6. [Mitigación de Crashes de Cámara](#6-mitigación-de-crashes-de-cámara)
7. [Protocolo de Escalada de Emergencias (v2.0)](#7-protocolo-de-escalada-de-emergencias-v20)
8. [Sistema de Audio Preventivo (AudioAlertManager)](#8-sistema-de-audio-preventivo-audioalertmanager)
9. [Onboarding y Experiencia de Usuario Premium](#9-onboarding-y-experiencia-de-usuario-premium)
10. [Seguridad y Reglas Firestore](#10-seguridad-y-reglas-firestore)
11. [Preguntas de Defensa Técnica](#11-preguntas-de-defensa-técnica)

---

## 1. El Problema

La somnolencia al volante es la **tercera causa de accidentes de tránsito mortales** en Latinoamérica. A diferencia de los sistemas de asistencia que integran los vehículos premium (coste > $30,000 USD), **no existe una solución de bajo costo, nativa, offline-first y sin dependencia de hardware externo** que detecte fatiga en tiempo real usando únicamente el teléfono inteligente del conductor.

**DriveSafe resuelve este problema:**
- ✅ Corre 100% en el dispositivo del conductor (Edge AI) — sin costos de servidor.
- ✅ Funciona sin conexión a Internet constante.
- ✅ Requiere solo una cámara frontal estándar (presente en el 100% de los smartphones modernos).
- ✅ Activa automáticamente una red de apoyo cuando el conductor no responde.

---

## 2. Stack Tecnológico Completo

| Capa | Tecnología | Justificación |
|------|-----------|---------------|
| **Lenguaje** | Kotlin 1.9 | Primer lenguaje oficial de Android, null-safety nativa |
| **UI Framework** | Jetpack Compose (Material3) | UI declarativa, reactividad state-driven, sin XML |
| **Concurrencia** | Kotlin Coroutines + Flow | Operaciones asíncronas sin bloquear el UI Thread |
| **Arquitectura** | MVVM + Repository Pattern | Separación de responsabilidades, testabilidad |
| **Visión Artificial** | Google ML Kit Face Detection | Modelo TFLite on-device, sin llamadas a API externa |
| **Cámara** | CameraX (Jetpack) | Abstracción del hardware, lifecycle-aware |
| **Autenticación** | Firebase Authentication | Gestión de sesión segura, OAuth/Email |
| **Base de datos** | Cloud Firestore (NoSQL) | Sincronización en tiempo real, offline persistence |
| **Mapas** | OSMDroid (OpenStreetMap) | Open source, sin API Key, sin costos de facturación |
| **Audio** | MediaPlayer (local res/raw) | Latencia < 50ms, sin TTS, funciona offline |
| **Background** | Foreground Service + CameraX | Protege el proceso del OOM Killer del SO |
| **Ubicación** | FusedLocationProviderClient | GPS + Network fusion para mayor precisión |
| **Persistencia local** | SharedPreferences + DataStore | Configuración local rápida y reactiva |

---

## 3. Arquitectura del Proyecto

Diseñada bajo los principios de **Clean Architecture** y **MVVM**, con capas claramente delimitadas:

```text
miguel.alejandro.edu.drivesafeam
│
├── 📄 DriveSafeApp.kt         → Application class. Inicializa Firebase y OSMDroid globalmente.
├── 📄 MainActivity.kt         → Single Activity Pattern. Aloja el NavHost de Compose Navigation.
│
├── 📂 data/
│   ├── local/
│   │   └── PreferenciasManager.kt    → DataStore para sesión persistente del usuario.
│   │
│   ├── model/
│   │   ├── Alerta.kt                 → Registro de incidentes (tipo, nivel, coordenadas, timestamp).
│   │   ├── ConfiguracionAlerta.kt    → Preferencias de usuario (v2.0: accionEmergencia + secondsBeforeAction).
│   │   ├── ContactoEmergencia.kt     → Nombre + teléfono del contacto SOS.
│   │   ├── EstadoDeteccion.kt        → Sealed class: Seguro | Advertencia | Peligro | Critico | SinRostro.
│   │   ├── PuntoMapa.kt              → Reportes comunitarios georreferenciados.
│   │   ├── TipoAccionEmergencia.kt   → Enum: WHATSAPP | LLAMADA | NINGUNA (exclusión mutua).
│   │   └── Usuario.kt                → Perfil del conductor registrado.
│   │
│   └── repository/
│       ├── AlertaRepository.kt       → CRUD de alertas en Firestore /users/{uid}/alertas.
│       ├── AuthRepository.kt         → Login, registro y sesión con Firebase Auth.
│       ├── ConfiguracionRepository.kt → Configuración personalizada en Firestore.
│       ├── PuntosMapaRepository.kt   → Reportes comunitarios con filtro temporal (3h).
│       ├── UbicacionRepository.kt    → GPS puntual via FusedLocationProviderClient.
│       └── UsuarioRepository.kt      → CRUD del perfil del conductor.
│
├── 📂 detection/
│   ├── AlertaManager.kt              → Vibración háptica (VibrationEffect) + alarma del sistema (legado).
│   ├── AudioAlertManager.kt          → ★ NUEVO: MediaPlayer local (res/raw) con callbacks de intent.
│   ├── CameraManager.kt              → Pipeline CameraX con backpressure y throttle 5–10 FPS.
│   └── FaceDetectionManager.kt       → Wrapper ML Kit: configuración del detector on-device.
│
├── 📂 navigation/
│   └── DriveSafeNavigation.kt        → Rutas selladas y NavHost con animaciones de transición.
│
├── 📂 service/
│   └── MonitoreoService.kt           → Foreground Service con notificación persistente + wakelock.
│
└── 📂 ui/
    ├── components/
    │   └── DriveSafeComponents.kt    → DriveSafeButton, DriveSafeTextField, DriveSafeCard, StatusIndicator…
    │
    ├── screens/
    │   ├── SplashScreen.kt           → Logo + DriveSafeGlow animado. Routing de sesión.
    │   ├── OnboardingScreen.kt       → ★ NUEVO: 3 slides con AnimatedContent + pill indicators.
    │   ├── PermisosScreen.kt         → Solicitud secuencial de permisos (Cámara → Notif → Ubicación).
    │   ├── LoginScreen.kt            → Auth premium con validación en tiempo real.
    │   ├── RegisterScreen.kt         → Registro con feedback de errores inline.
    │   ├── MonitoreoScreen.kt        → Dashboard principal: PowerButton + CameraPreview + StatusGlow.
    │   ├── MapaScreen.kt             → Mapa OSMDroid con marcadores Canvas + FloatingActionButton.
    │   ├── HistorialScreen.kt        → Timeline de alertas con dots de color por nivel.
    │   ├── ConfiguracionScreen.kt    → ★ ACTUALIZADO: RadioButtons de acción exclusiva + timer único.
    │   └── EmergenciaScreen.kt       → Pantalla crítica con fondo rojo pulsante.
    │
    ├── theme/
    │   ├── Color.kt                  → 5 capas de elevación (Surface0–4) + colores semánticos.
    │   ├── Type.kt                   → Escala tipográfica premium con letter-spacing.
    │   ├── Theme.kt                  → ColorScheme M3 completo, formas redondeadas.
    │   └── DriveSafeGlow.kt          → Efecto glow radiante animado (breathing canvas).
    │
    └── viewmodel/
        ├── MonitoreoViewModel.kt     → ★ REFACTORIZADO: Máquina de estados + AudioAlertManager + SOS v2.
        ├── ConfiguracionViewModel.kt → ★ ACTUALIZADO: updateAccionEmergencia() exclusiva.
        ├── HistorialViewModel.kt
        ├── LoginViewModel.kt
        ├── MapaViewModel.kt
        └── RegisterViewModel.kt
```

---

## 4. Esquema Jerárquico de Firestore

```text
/users  (Colección raíz — solo usuarios autenticados)
  └── {userId}
        ├── nombre: "Miguel Alejandro"
        ├── correo: "ale@example.com"
        ├── contactoEmergencia:
        │     ├── nombre: "Mamá"
        │     └── telefono: "+573001234567"
        │
        ├── configuracion:            ← ConfiguracionAlerta serializada
        │     ├── sonidoActivo: true
        │     ├── vibracionActiva: true
        │     ├── accionEmergencia: "WHATSAPP"   ← v2.0: enum como String
        │     ├── secondsBeforeAction: 15         ← v2.0: timer único
        │     └── ... (resto de campos)
        │
        └── /alertas  (Subcolección privada — solo el propio usuario puede acceder)
              └── {alertaId}
                    ├── tipo: "DROWSINESS" | "DISTRACTION" | "NO_FACE"
                    ├── nivel: "ADVERTENCIA" | "PELIGRO" | "CRITICO"
                    ├── latitud: 4.6097
                    ├── longitud: -74.0817
                    └── timestamp: 1779506400000

/reportes  (Colección comunitaria — cualquier usuario autenticado puede leer/escribir)
  └── {reporteId}
        ├── userId: "authUid"
        ├── latitude: 4.6123
        ├── longitude: -74.0850
        ├── descripcion: "Accidente en curva peligrosa"
        └── timestamp: 1779506400000
```

> **¿Por qué subcolección y no colección global?**
> Declarar `/users/{userId}/alertas` permite reglas de Firestore `allow read, write: if request.auth.uid == userId`. Un atacante autenticado **no puede descargar** el historial de otros conductores aunque conozca su UID.

---

## 5. Algoritmo de Edge AI — ML Kit

Todo el procesamiento ocurre **en el dispositivo** (Edge Computing). Cero latencia de red, cero privacidad comprometida.

### Pipeline de detección

```
CameraX (10 FPS) → FaceDetectionManager → procesarRostros() → Máquina de Estados
```

### Configuración del detector ML Kit

```kotlin
FaceDetectorOptions.Builder()
    .setPerformanceMode(PERFORMANCE_MODE_FAST)      // Velocidad > precisión de contorno
    .setClassificationMode(CLASSIFICATION_MODE_ALL) // Probabilidades oculares + sonrisa
    .enableTracking()                               // ID único por rostro entre frames
    .setLandmarkMode(LANDMARK_MODE_NONE)            // Desactivado: no necesitamos puntos faciales
    .setContourMode(CONTOUR_MODE_NONE)              // Desactivado: reduce uso de CPU ~40%
    .build()
```

### Tabla de umbrales de la Máquina de Estados

| Condición | Medición | Umbral de Tiempo | Estado | Acción |
|-----------|----------|-----------------|--------|--------|
| Pérdida de rostro | 0 caras en cámara | > 10 frames (~2.0s) | `SinRostro` | Audio + vibración |
| Somnolencia | Prob. ojo < 20% en ambos ojos | > 3 frames (~0.6s) | `Peligro` | Audio + vibración + flash + SOS timer |
| Distracción | ‖headEulerY‖ > 20° ó ‖headEulerZ‖ > 20° | > 5 frames (~1.0s) | `Advertencia` | Audio + vibración |
| Recuperación | Ojos abiertos + cabeza centrada | > 10 frames (~2.0s) | `Seguro` | Cancelar todas las alertas |

### Filtro de histéresis
Para pasar de cualquier estado de alerta a `Seguro`, el conductor necesita **10 frames perfectos consecutivos** (~2 segundos). Esto **elimina falsos positivos** causados por parpadeos normales o micromovimientos involuntarios.

### Throttle de cámara (ahorro de batería)
```kotlin
// En CameraManager.kt — analiza solo 1 frame cada 200ms (5 FPS efectivos)
if (System.currentTimeMillis() - lastAnalyzedTimestamp < monitoringIntervalMs) {
    imageProxy.close()
    return
}
```

---

## 6. Mitigación de Crashes de Cámara

### Problema
CameraX comparte recursos del hardware con otros procesos del SO. Al pasar la app a background, Android puede liberar la cámara, causando `CameraAccessException` o crashes silenciosos.

### Soluciones implementadas

| Técnica | Archivo | Descripción |
|---------|---------|-------------|
| **Backpressure KEEP_ONLY_LATEST** | `CameraManager.kt` | Si el análisis ML tarda más que el framerate, descarta frames viejos en lugar de acumularlos en memoria |
| **Foreground Service** | `MonitoreoService.kt` | Eleva la prioridad del proceso al nivel de app en pantalla, previniendo el OOM Killer |
| **Lifecycle-aware** | `CameraManager.startCamera(lifecycleOwner)` | CameraX destruye y recrea el pipeline automáticamente según el ciclo de vida |
| **Marcadores Canvas (no VectorDrawable)** | `MapaScreen.kt` | Los marcadores vectoriales XML crashean en el motor OpenGL de OSMDroid. Se dibuja programáticamente sobre un Bitmap ARGB_8888 |
| **DisposableEffect** | `MonitoreoScreen.kt` | Llama a `cameraManager.shutdown()` y detiene el service cuando el Composable sale del árbol |

---

## 7. Protocolo de Escalada de Emergencias (v2.0)

### Cambio arquitectónico principal: Exclusión Mutua

**v1.0 (anterior):** Dos timers independientes (`secondsBeforeWhatsapp`, `secondsBeforeCall`) que se activaban secuencialmente, causando que el intent de WhatsApp robara el foco **antes** de la llamada, con el riesgo de que ambos se solaparan.

**v2.0 (actual):** Un único enum `TipoAccionEmergencia { WHATSAPP, LLAMADA, NINGUNA }` que el usuario configura con un RadioButton de selección exclusiva. **Una sola acción se ejecuta**, sin posibilidad de solapamiento.

### Diagrama de flujo completo

```
[Detección de Peligro sostenida]
          │
          ▼
[audioAlertManager.playAtencionVolante()]
          │  Audio "Por favor, presta atención al volante"
          │
          ▼
[Iniciar SOS Timer (viewModelScope.launch)]
          │
          │  ← Conductor responde → Resetear, cancelar timer
          │
          │  t = secondsBeforeAction (ej: 15s)
          ▼
[Leer accionEmergencia desde SharedPreferences]
          │
     ┌────┴────────────────┐
     │                     │
  WHATSAPP               LLAMADA                NINGUNA
     │                     │                      │
     ▼                     ▼                      ▼
[playActivaWhatsapp()]  [playActivaLlamada()]  [No-op]
[Audio: "Ejecutando     [Audio: "Ejecutando
 WhatsApp"]              llamada"]
     │                     │
     │ OnCompletion         │ OnCompletion
     ▼                     ▼
[Construir URL wa.me   [Intent ACTION_DIAL]
 con formato bold/italic]
[URLEncoder.encode()]
[Intent ACTION_VIEW]
```

### Formato enriquecido del mensaje WhatsApp (Tarea 2)

```
¡Hola, {nombre_contacto}! 👋 Soy *{nombre_usuario}*. _Este es un mensaje de emergencia automático de la app *DriveSafe*._ No me siento muy bien, mi ubicación actual es: https://maps.google.com/?q={lat},{lng}
```

- `* texto *` → **negrita** nativa de WhatsApp
- `_ texto _` → *cursiva* nativa de WhatsApp
- Codificado con `java.net.URLEncoder.encode(mensaje, "UTF-8")` (no `Uri.encode`) para compatibilidad completa con la API `wa.me`

---

## 8. Sistema de Audio Preventivo (AudioAlertManager)

### Problema con TTS
`TextToSpeech.speak()` tiene una latencia de síntesis de **300–800ms** dependiendo del motor instalado en el dispositivo. En emergencias de conducción, este retraso es inaceptable.

### Solución: MediaPlayer local

```kotlin
class AudioAlertManager(context) {

    // Reproduce res/raw/atencion_volante.mp3 inmediatamente
    fun playAtencionVolante(nivel, vibracionActivada)

    // Reproduce res/raw/activa_whatsapp.mp3 → callback cuando termina
    fun playActivaWhatsapp(onReady: () -> Unit)

    // Reproduce res/raw/activa_llamada.mp3 → callback cuando termina
    fun playActivaLlamada(onReady: () -> Unit)
}
```

**Archivos requeridos en `res/raw/`:**

| Archivo | Texto                                                    | Cuándo suena |
|---------|----------------------------------------------------------|-------------|
| `atencion_volante.mp3` | "Por favor, ${nombreUsuario} presta atención al volante" | Al detectar fatiga inicial |
| `activa_whatsapp.mp3` | "Ejecutando mensaje de WhatsApp"                         | Justo antes del intent WhatsApp |
| `activa_llamada.mp3` | "Ejecutando llamada de emergencia"                       | Justo antes del intent de llamada |

**Patrón crítico:**
```kotlin
audioAlertManager.playActivaWhatsapp {
    // Este bloque solo se ejecuta cuando el mp3 TERMINÓ de sonar
    context.startActivity(whatsappIntent)
}
```

El intent de salida nunca se lanza mientras el audio está reproduciéndose. Si `MediaPlayer.create()` falla, el callback se invoca igualmente (fail-safe), asegurando que la emergencia nunca quede bloqueada por un error de audio.

---

## 9. Onboarding y Experiencia de Usuario Premium

### OnboardingScreen (v2.0)

- Implementada con `AnimatedContent` y transiciones `slideInHorizontally` para reemplazar `HorizontalPager` (más control programático sobre el estado de página).
- 3 slides con títulos impactantes y descripciones breves:
  1. **"Monitoreo Inteligente"** — IA en el dispositivo
  2. **"Alertas en Tiempo Real"** — Visual + háptica + sonido
  3. **"Asistencia de Emergencia"** — Red de apoyo automática
- Indicadores de progreso tipo **pill** (cápsula activa) con animación de ancho.
- Fondo con gradiente ambiental naranja y respaldo oscuro puro.

### Sistema de diseño "Premium Dark"

| Token | Valor | Uso |
|-------|-------|-----|
| `DriveSafeDark` | `#080808` | Fondo absoluto |
| `DriveSafeOrange` | `#FF6D00` | CTAs, estados activos |
| `DriveSafeGreen` | `#00E676` | Estado Seguro |
| `DriveSafeYellow` | `#FFD600` | Estado Advertencia |
| `DriveSafeRed` | `#FF1744` | Estado Peligro / SOS |
| `Surface0–Surface4` | `#0F0F0F–#2C2C2C` | Capas de elevación |

---

## 10. Seguridad y Reglas Firestore

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Perfil e historial: solo el propio conductor
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;

      match /alertas/{alertaId} {
        allow read, write: if request.auth != null && request.auth.uid == userId;
      }
    }

    // Reportes comunitarios: cualquier usuario autenticado
    match /reportes/{reporteId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

**Expiración automática de reportes del mapa:**
```kotlin
// PuntosMapaRepository.kt — filtro del lado del servidor
val tiempoLimite = System.currentTimeMillis() - (3 * 60 * 60 * 1000L)
collection.whereGreaterThan("timestamp", tiempoLimite).addSnapshotListener { ... }
```

---

## 11. Preguntas de Defensa Técnica

### ❓ ¿Por qué Edge AI y no un servidor de visión artificial?
Latencia cero. Una API de visión remota introduce > 100–500ms de roundtrip. En detección de microsueño (umbral de 0.6s), esa latencia descartaría casi la mitad de la ventana de reacción. Además, no requiere datos de video enviados a la nube → **privacidad absoluta**.

### ❓ ¿Qué pasa si el conductor tapa la cámara?
El detector ML Kit deja de reportar rostros. Tras 10 frames sin detección (~2s), el sistema transita a `EstadoDeteccion.SinRostro` y reproduce el audio de atención al volante. Si no hay respuesta en `secondsBeforeAction` segundos, ejecuta el protocolo SOS.

### ❓ ¿Por qué `TipoAccionEmergencia` en lugar de dos timers independientes?
Con dos timers, el intent de WhatsApp robaba el foco de la app antes de que la llamada se iniciara, causando una secuencia confusa. Peor aún, si la app perdía el foco hacia WhatsApp, el timer de llamada podía fallar silenciosamente. La exclusión mutua garantiza **exactamente un** intent de salida con su audio de confirmación previa.

### ❓ ¿Por qué MediaPlayer en lugar de TTS?
TTS requiere el motor de síntesis del dispositivo (no siempre disponible en español), tiene 300–800ms de latencia de síntesis y no funciona de forma confiable offline. Los `.mp3` locales en `res/raw/` inician en < 50ms y son idénticos en todos los dispositivos.

### ❓ ¿Cómo se evita que la base de datos crezca indefinidamente?
Doble protección: (1) filtro del cliente que solo descarga reportes con `timestamp > now - 3h`, y (2) posibilidad de configurar una Cloud Function programada que purgue documentos obsoletos directamente en Firestore.

### ❓ ¿Por qué OSMDroid en lugar de Google Maps SDK?
OSMDroid es open source, no requiere API Key ni facturación por volumen de consultas. Para un MVP universitario, elimina el riesgo de cargos inesperados si el mapa registra muchas visualizaciones.

### ❓ ¿Qué hace el Foreground Service exactamente?
`MonitoreoService` llama a `startForeground(1, notification)`, lo que mueve el proceso de la app al nivel de prioridad **foreground** del OOM Killer de Android. Esto le dice al SO que trate la app como si estuviera en pantalla, evitando que la liquide para liberar RAM mientras el conductor tiene el teléfono en el soporte del auto.
