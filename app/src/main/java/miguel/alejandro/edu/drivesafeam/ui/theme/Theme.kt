package miguel.alejandro.edu.drivesafeam.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════════════════
// DriveSafe Theme — Premium Dark Mode Color Scheme
// ═══════════════════════════════════════════════════════════════

private val DriveSafeColorScheme = darkColorScheme(
    // Acento principal
    primary = DriveSafeOrange,
    onPrimary = Color.White,
    primaryContainer = DriveSafeOrangeSubtle,
    onPrimaryContainer = DriveSafeOrangeLight,

    // Acento secundario (naranja claro)
    secondary = DriveSafeOrangeLight,
    onSecondary = Color.White,
    secondaryContainer = DriveSafeOrangeSubtle,
    onSecondaryContainer = DriveSafeOrangeSoft,

    // Terciario (verde seguridad)
    tertiary = DriveSafeGreen,
    onTertiary = Color.Black,
    tertiaryContainer = DriveSafeGreenSoft,
    onTertiaryContainer = DriveSafeGreen,

    // Fondos
    background = DriveSafeDark,
    onBackground = TextWhite,

    // Superficies (capas de elevación)
    surface = DriveSafeSurface0,
    onSurface = TextWhite,
    surfaceVariant = DriveSafeSurface2,
    onSurfaceVariant = TextGray,
    surfaceTint = DriveSafeOrange,

    // Inversas
    inverseSurface = TextWhite,
    inverseOnSurface = DriveSafeDark,
    inversePrimary = DriveSafeOrange,

    // Error
    error = DriveSafeRed,
    onError = Color.White,
    errorContainer = DriveSafeRedSoft,
    onErrorContainer = DriveSafeRed,

    // Contorno (outline)
    outline = BorderGray,
    outlineVariant = BorderSubtle
)

// ═══════════════════════════════════════════════════════════════
// Sistema de Formas — Bordes Premium
// ═══════════════════════════════════════════════════════════════

private val DriveSafeShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),      // Chips, badges
    small = RoundedCornerShape(10.dp),           // Small buttons
    medium = RoundedCornerShape(16.dp),          // Cards, dialogs
    large = RoundedCornerShape(24.dp),           // Bottom sheets, big cards
    extraLarge = RoundedCornerShape(32.dp)       // FAB, hero elements
)

@Composable
fun DrivesafeamTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Fondo inmersivo: navigation y status bar se funden con el fondo
            window.statusBarColor = DriveSafeDark.toArgb()
            window.navigationBarColor = DriveSafeDark.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = DriveSafeColorScheme,
        typography = Typography,
        shapes = DriveSafeShapes,
        content = content
    )
}