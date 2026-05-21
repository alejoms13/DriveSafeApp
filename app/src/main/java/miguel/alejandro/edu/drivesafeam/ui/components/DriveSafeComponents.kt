package miguel.alejandro.edu.drivesafeam.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import miguel.alejandro.edu.drivesafeam.ui.theme.*

// Modifier extension for custom premium glow effects
fun Modifier.glow(
    color: Color,
    radius: Dp = 10.dp,
    shapeRadius: Dp = 20.dp
) = this.drawBehind {
    drawIntoCanvas { canvas ->
        val paint = Paint()
        val frameworkPaint = paint.asFrameworkPaint()
        frameworkPaint.color = Color.Transparent.toArgb()
        frameworkPaint.setShadowLayer(
            radius.toPx(),
            0f,
            0f,
            color.toArgb()
        )
        canvas.drawRoundRect(
            0f,
            0f,
            size.width,
            size.height,
            shapeRadius.toPx(),
            shapeRadius.toPx(),
            paint
        )
    }
}

@Composable
fun DriveSafeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .glow(if (enabled) DriveSafeOrange.copy(alpha = 0.3f) else Color.Transparent, radius = 12.dp, shapeRadius = 28.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        enabled = enabled,
        contentPadding = PaddingValues()
    ) {
        val alpha = if (enabled) 1f else 0.5f
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            DriveSafeOrange.copy(alpha = alpha),
                            DriveSafeOrangeLight.copy(alpha = alpha)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = TextWhite.copy(alpha = alpha),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun DriveSafeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var passwordVisible by remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, BorderGray, RoundedCornerShape(14.dp)),
        placeholder = {
            Text(placeholder, color = TextGray, fontSize = 15.sp)
        },
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Mostrar contraseña",
                        tint = TextGray
                    )
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = DriveSafeCardLight,
            unfocusedContainerColor = DriveSafeCardLight,
            focusedIndicatorColor = DriveSafeOrange,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = DriveSafeOrange,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite
        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
        singleLine = true
    )
}

@Composable
fun DriveSafeCard(
    modifier: Modifier = Modifier,
    glowColor: Color = Color.Transparent,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DriveSafeCard)
            .border(1.dp, BorderGray, RoundedCornerShape(20.dp))
            .then(
                if (glowColor != Color.Transparent) {
                    Modifier.glow(glowColor, radius = 8.dp, shapeRadius = 20.dp)
                } else Modifier
            )
            .padding(16.dp),
        content = content
    )
}

enum class DetectionState {
    SAFE,       // Verde respirando
    WARNING,    // Amarillo pulsante rápido
    DANGER,     // Rojo constante vibrando/shake
    OFF         // Apagado
}

@Composable
fun StatusIndicator(
    state: DetectionState,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "GlowTransition")
    
    // Configuración de animaciones de glow según estado
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    DetectionState.SAFE -> 1500
                    DetectionState.WARNING -> 600
                    DetectionState.DANGER -> 250
                    else -> 1000
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowScale"
    )

    val (color, text, glowColor) = when (state) {
        DetectionState.SAFE -> Triple(DriveSafeGreen, "CONDUCCIÓN SEGURA", DriveSafeGreenGlow)
        DetectionState.WARNING -> Triple(DriveSafeYellow, "ADVERTENCIA: DISTRACCIÓN", DriveSafeYellowGlow)
        DetectionState.DANGER -> Triple(DriveSafeRed, "PELIGRO: SOMNOLENCIA", DriveSafeRedGlow)
        DetectionState.OFF -> Triple(TextGray, "MONITOREO DESACTIVADO", Color.Transparent)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(DriveSafeCard)
            .border(1.dp, if (state != DetectionState.OFF) color else BorderGray, RoundedCornerShape(28.dp))
            .glow(glowColor.copy(alpha = glowColor.alpha * scale), radius = 12.dp, shapeRadius = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (state != DetectionState.OFF) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(color)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                color = color,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.25.sp
            )
        }
    }
}

@Composable
fun DriveSafeDisclaimer(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DriveSafeCardLight)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Aviso legal",
            tint = DriveSafeOrange,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "DriveSafe es una herramienta de apoyo y no reemplaza la atención del conductor.",
            color = TextGray,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )
    }
}
