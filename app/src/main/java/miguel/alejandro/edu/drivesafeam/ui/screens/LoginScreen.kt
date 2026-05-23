package miguel.alejandro.edu.drivesafeam.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import miguel.alejandro.edu.drivesafeam.R
import miguel.alejandro.edu.drivesafeam.data.local.PreferenciasManager
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeButton
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeDivider
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeOutlineButton
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeTextField
import miguel.alejandro.edu.drivesafeam.ui.theme.*
import miguel.alejandro.edu.drivesafeam.ui.viewmodel.AuthState
import miguel.alejandro.edu.drivesafeam.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()

    val context = LocalContext.current
    val prefManager = remember { PreferenciasManager(context) }

    // ── Animaciones de entrada ──
    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        contentVisible = true
    }

    // ── Navegación en éxito ──
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            val uid = authViewModel.getCurrentUserId() ?: ""
            prefManager.guardarIdUsuario(uid)
            val nombre = correo.substringBefore("@")
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            prefManager.guardarNombreUsuario(nombre)
            onLoginSuccess()
            authViewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DriveSafeDark)
    ) {
        // ── Glow ambiental naranja sutil en la parte superior ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DriveSafeOrange.copy(alpha = 0.06f),
                            DriveSafeOrange.copy(alpha = 0.02f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // ── Logo con entrada animada ──
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(600)) + slideInVertically(
                    initialOffsetY = { -40 },
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "DriveSafe Logo",
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Bienvenido",
                        style = MaterialTheme.typography.displayMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Inicia sesión para proteger tu viaje",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextGray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // ── Formulario con entrada escalonada ──
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(600, delayMillis = 200)) + slideInVertically(
                    initialOffsetY = { 30 },
                    animationSpec = tween(600, delayMillis = 200, easing = FastOutSlowInEasing)
                )
            ) {
                Column {
                    DriveSafeTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        placeholder = "Correo electrónico",
                        leadingIcon = Icons.Outlined.Email,
                        keyboardType = KeyboardType.Email,
                        isError = authState is AuthState.Error && correo.isBlank()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    DriveSafeTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it },
                        placeholder = "Contraseña",
                        isPassword = true,
                        leadingIcon = Icons.Outlined.Lock,
                        isError = authState is AuthState.Error && contrasena.isBlank()
                    )

                    // ── Mensajes (Error / Éxito) ──
                    AnimatedVisibility(
                        visible = authState is AuthState.Error || authState is AuthState.PasswordResetSent,
                        enter = fadeIn(tween(200)) + expandVertically(),
                        exit = fadeOut(tween(200)) + shrinkVertically()
                    ) {
                        val message = when (authState) {
                            is AuthState.Error -> (authState as AuthState.Error).message
                            is AuthState.PasswordResetSent -> (authState as AuthState.PasswordResetSent).message
                            else -> ""
                        }
                        val textColor = if (authState is AuthState.PasswordResetSent) DriveSafeOrange else DriveSafeRed

                        Text(
                            text = message,
                            color = textColor,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp, start = 4.dp),
                            textAlign = TextAlign.Start
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Forgot password link ──
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = DriveSafeOrange,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { authViewModel.resetPassword(correo) }
                            .padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // ── Botón principal ──
                    DriveSafeButton(
                        text = "Iniciar Sesión",
                        onClick = { authViewModel.login(correo, contrasena) },
                        enabled = authState !is AuthState.Loading,
                        isLoading = authState is AuthState.Loading
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Separador ──
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(600, delayMillis = 400))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    DriveSafeDivider(text = "o continúa con")

                    Spacer(modifier = Modifier.height(32.dp))

                    // ── Link a registro ──
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "¿No tienes una cuenta? ",
                            color = TextGray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Regístrate",
                            color = DriveSafeOrange,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onNavigateToRegister() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
