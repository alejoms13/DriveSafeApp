package miguel.alejandro.edu.drivesafeam.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun DriveSafeGlow(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val radius = size.minDimension / 1.5f
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = 0.35f),
                    color.copy(alpha = 0.15f),
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
