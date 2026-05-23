package miguel.alejandro.edu.drivesafeam.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Efecto de glow radiante ambiental premium.
 * Se usa como fondo de elementos hero (Splash logo, Dashboard).
 * Incluye una respiración sutil animada.
 */
@Composable
fun DriveSafeGlow(
    color: Color,
    modifier: Modifier = Modifier,
    animated: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "GlowBreathing")

    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreatheScale"
    )

    val breatheAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreatheAlpha"
    )

    val scale = if (animated) breatheScale else 1f
    val alpha = if (animated) breatheAlpha else 0.3f

    Canvas(modifier = modifier.fillMaxSize()) {
        val radius = (size.minDimension / 1.4f) * scale
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = alpha),
                    color.copy(alpha = alpha * 0.4f),
                    color.copy(alpha = alpha * 0.1f),
                    Color.Transparent
                ),
                center = Offset(size.width / 2f, size.height / 2f),
                radius = radius
            ),
            radius = radius,
            center = Offset(size.width / 2f, size.height / 2f)
        )
    }
}
