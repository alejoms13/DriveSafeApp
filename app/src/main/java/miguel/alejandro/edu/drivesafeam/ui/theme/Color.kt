package miguel.alejandro.edu.drivesafeam.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════
// Paleta Oficial DriveSafe — Premium Dark Mode System
// ═══════════════════════════════════════════════════════════════

// ── Naranja Principal (Acento) ──────────────────────────────
val DriveSafeOrange = Color(0xFFFF6D00)        // Primary CTA
val DriveSafeOrangeLight = Color(0xFFFF9E40)    // Hover / Active states
val DriveSafeOrangeSoft = Color(0xFFFF8A50)     // Secondary text accent
val DriveSafeOrangeGlow = Color(0x33FF6D00)     // Ambient glow (20% alpha)
val DriveSafeOrangeSubtle = Color(0x1AFF6D00)   // Ultra-subtle tint (10%)

// ── Superficies Oscuras (Capas de Elevación) ────────────────
val DriveSafeDark = Color(0xFF080808)           // Base: fondo absoluto
val DriveSafeSurface0 = Color(0xFF0F0F0F)       // Elevation 0: fondo principal
val DriveSafeSurface1 = Color(0xFF151515)       // Elevation 1: cards
val DriveSafeSurface2 = Color(0xFF1C1C1C)       // Elevation 2: inputs / cards hover
val DriveSafeSurface3 = Color(0xFF242424)       // Elevation 3: dropdowns / dialogs
val DriveSafeSurface4 = Color(0xFF2C2C2C)       // Elevation 4: elevated elements

// Legacy aliases for backward compat
val DriveSafeCard = DriveSafeSurface1
val DriveSafeCardLight = DriveSafeSurface2

// ── Estados de Seguridad ────────────────────────────────────
val DriveSafeGreen = Color(0xFF00E676)          // Conducción segura
val DriveSafeGreenGlow = Color(0x3300E676)
val DriveSafeGreenSoft = Color(0x1A00E676)

val DriveSafeYellow = Color(0xFFFFD600)         // Advertencia
val DriveSafeYellowGlow = Color(0x33FFD600)
val DriveSafeYellowSoft = Color(0x1AFFD600)

val DriveSafeRed = Color(0xFFFF1744)            // Peligro / Alerta
val DriveSafeRedGlow = Color(0x4DFF1744)
val DriveSafeRedSoft = Color(0x1AFF1744)

// ── Tipografía / Texto ──────────────────────────────────────
val TextWhite = Color(0xFFF5F5F5)               // High emphasis
val TextWhiteMedium = Color(0xFFE0E0E0)         // Medium emphasis
val TextGray = Color(0xFF8A8A8A)                // Low emphasis / placeholders
val TextGrayLight = Color(0xFFBDBDBD)           // Secondary text
val TextGrayDark = Color(0xFF616161)            // Disabled / hint

// ── Bordes y Separadores ────────────────────────────────────
val BorderSubtle = Color(0xFF1F1F1F)            // Borde ultra-sutil
val BorderGray = Color(0xFF2A2A2A)              // Borde normal
val BorderFocused = Color(0xFF3A3A3A)           // Borde enfocado
val DividerColor = Color(0xFF1A1A1A)            // Separadores