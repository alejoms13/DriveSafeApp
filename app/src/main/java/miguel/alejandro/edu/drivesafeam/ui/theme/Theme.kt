package miguel.alejandro.edu.drivesafeam.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DriveSafeColorScheme = darkColorScheme(
    primary = DriveSafeOrange,
    onPrimary = TextWhite,
    primaryContainer = DriveSafeOrangeGlow,
    secondary = DriveSafeOrangeLight,
    onSecondary = TextWhite,
    background = DriveSafeDark,
    onBackground = TextWhite,
    surface = DriveSafeCard,
    onSurface = TextWhite,
    surfaceVariant = DriveSafeCardLight,
    onSurfaceVariant = TextGray,
    error = DriveSafeRed,
    onError = TextWhite
)

@Composable
fun DrivesafeamTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DriveSafeDark.toArgb()
            window.navigationBarColor = DriveSafeDark.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = DriveSafeColorScheme,
        typography = Typography,
        content = content
    )
}