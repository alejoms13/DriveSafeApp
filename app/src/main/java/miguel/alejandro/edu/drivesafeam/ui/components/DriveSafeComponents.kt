package miguel.alejandro.edu.drivesafeam.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import miguel.alejandro.edu.drivesafeam.ui.theme.*

// ═══════════════════════════════════════════════════════════════
//  MODIFIER EXTENSIONS — Efectos Premium
// ═══════════════════════════════════════════════════════════════

/**
 * Efecto glow (sombra radiante) para elementos destacados.
 * Usa setShadowLayer del framework Paint para un halo difuso.
 */
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

// ═══════════════════════════════════════════════════════════════
//  BOTÓN PRINCIPAL — Gradiente Naranja con Glow
// ═══════════════════════════════════════════════════════════════

@Composable
fun DriveSafeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "ButtonScale"
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (enabled && !isLoading) 0.4f else 0f,
        animationSpec = tween(300),
        label = "GlowAlpha"
    )

    Button(
        onClick = { if (!isLoading) onClick() },
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .glow(
                DriveSafeOrange.copy(alpha = glowAlpha),
                radius = 16.dp,
                shapeRadius = 29.dp
            ),
        shape = RoundedCornerShape(29.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        enabled = enabled,
        interactionSource = interactionSource,
        contentPadding = PaddingValues()
    ) {
        val contentAlpha = if (enabled) 1f else 0.4f

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            DriveSafeOrange.copy(alpha = contentAlpha),
                            DriveSafeOrangeLight.copy(alpha = contentAlpha)
                        )
                    ),
                    shape = RoundedCornerShape(29.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(
                    text = text.uppercase(),
                    color = Color.White.copy(alpha = contentAlpha),
                    style = MaterialTheme.typography.labelLarge,
                    letterSpacing = 1.2.sp
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
//  BOTÓN SECUNDARIO — Outline / Ghost
// ═══════════════════════════════════════════════════════════════

@Composable
fun DriveSafeOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(29.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = DriveSafeOrange,
            disabledContentColor = TextGrayDark
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = if (enabled) DriveSafeOrange.copy(alpha = 0.6f) else BorderGray
        ),
        enabled = enabled
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            letterSpacing = 1.2.sp
        )
    }
}

// ═══════════════════════════════════════════════════════════════
//  TEXT FIELD — Input Premium con Label Flotante
// ═══════════════════════════════════════════════════════════════

@Composable
fun DriveSafeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: ImageVector? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    supportingText: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> DriveSafeRed
            isFocused -> DriveSafeOrange
            else -> BorderGray
        },
        animationSpec = tween(200),
        label = "BorderColor"
    )

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            placeholder = {
                Text(
                    placeholder,
                    color = TextGrayDark,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = if (isFocused) DriveSafeOrange else TextGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else null,
            trailingIcon = {
                if (isPassword) {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            tint = if (isFocused) TextGrayLight else TextGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                } else if (isError) {
                    Icon(
                        imageVector = Icons.Outlined.ErrorOutline,
                        contentDescription = "Error",
                        tint = DriveSafeRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation()
            else
                VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            interactionSource = interactionSource,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DriveSafeSurface2,
                unfocusedContainerColor = DriveSafeSurface1,
                errorContainerColor = DriveSafeRedSoft,
                focusedBorderColor = DriveSafeOrange,
                unfocusedBorderColor = BorderSubtle,
                errorBorderColor = DriveSafeRed,
                cursorColor = DriveSafeOrange,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhiteMedium,
                errorTextColor = TextWhite
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
            singleLine = true,
            isError = isError,
            shape = RoundedCornerShape(14.dp)
        )

        // Error o supporting text debajo del input
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                color = DriveSafeRed,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        } else if (supportingText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = supportingText,
                color = TextGrayDark,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
//  CARD — Tarjeta Premium con Bordes Sutiles
// ═══════════════════════════════════════════════════════════════

@Composable
fun DriveSafeCard(
    modifier: Modifier = Modifier,
    glowColor: Color = Color.Transparent,
    surfaceColor: Color = DriveSafeSurface1,
    borderColor: Color = BorderSubtle,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (glowColor != Color.Transparent) {
                    Modifier.glow(glowColor, radius = 8.dp, shapeRadius = 20.dp)
                } else Modifier
            )
            .clip(RoundedCornerShape(20.dp))
            .background(surfaceColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(20.dp),
        content = content
    )
}

// ═══════════════════════════════════════════════════════════════
//  STATUS INDICATOR — Barra de Estado Animada
// ═══════════════════════════════════════════════════════════════

enum class DetectionState {
    SAFE,       // Verde: respiración lenta
    WARNING,    // Amarillo: pulso rápido
    DANGER,     // Rojo: vibración constante
    OFF         // Gris: apagado
}

@Composable
fun StatusIndicator(
    state: DetectionState,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "StatusGlow")

    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    DetectionState.SAFE -> 2000
                    DetectionState.WARNING -> 600
                    DetectionState.DANGER -> 250
                    DetectionState.OFF -> 1500
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

    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    DetectionState.SAFE -> 2000
                    DetectionState.WARNING -> 500
                    DetectionState.DANGER -> 200
                    DetectionState.OFF -> 1000
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DotPulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .then(
                if (state != DetectionState.OFF) {
                    Modifier.glow(
                        glowColor.copy(alpha = glowColor.alpha * glowScale),
                        radius = 12.dp,
                        shapeRadius = 26.dp
                    )
                } else Modifier
            )
            .clip(RoundedCornerShape(26.dp))
            .background(
                if (state != DetectionState.OFF)
                    color.copy(alpha = 0.08f)
                else
                    DriveSafeSurface1
            )
            .border(
                1.dp,
                if (state != DetectionState.OFF)
                    color.copy(alpha = 0.3f)
                else
                    BorderSubtle,
                RoundedCornerShape(26.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (state != DetectionState.OFF) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = dotAlpha))
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = text,
                color = color,
                style = MaterialTheme.typography.labelMedium,
                letterSpacing = 1.5.sp
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
//  DISCLAIMER — Aviso Legal Premium
// ═══════════════════════════════════════════════════════════════

@Composable
fun DriveSafeDisclaimer(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DriveSafeOrangeSubtle)
            .border(1.dp, DriveSafeOrange.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Aviso legal",
            tint = DriveSafeOrange,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "DriveSafe es una herramienta de apoyo y no reemplaza la atención del conductor.",
            color = TextGray,
            style = MaterialTheme.typography.bodySmall,
            lineHeight = 16.sp
        )
    }
}

// ═══════════════════════════════════════════════════════════════
//  DIVIDER — Separador Sutil Premium
// ═══════════════════════════════════════════════════════════════

@Composable
fun DriveSafeDivider(
    modifier: Modifier = Modifier,
    text: String? = null
) {
    if (text != null) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 0.5.dp,
                color = BorderGray
            )
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = TextGrayDark,
                style = MaterialTheme.typography.labelSmall
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                thickness = 0.5.dp,
                color = BorderGray
            )
        }
    } else {
        HorizontalDivider(
            modifier = modifier,
            thickness = 0.5.dp,
            color = BorderGray
        )
    }
}

// ═══════════════════════════════════════════════════════════════
//  ICON BUTTON — Botón de Ícono Estilizado
// ═══════════════════════════════════════════════════════════════

@Composable
fun DriveSafeIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = TextGray,
    backgroundColor: Color = DriveSafeSurface2
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
    }
}
