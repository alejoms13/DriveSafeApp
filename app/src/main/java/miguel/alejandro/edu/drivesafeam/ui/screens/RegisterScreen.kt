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
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import miguel.alejandro.edu.drivesafeam.R
import miguel.alejandro.edu.drivesafeam.data.local.PreferenciasManager
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeButton
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeTextField
import miguel.alejandro.edu.drivesafeam.ui.theme.*
import miguel.alejandro.edu.drivesafeam.ui.viewmodel.AuthState
import miguel.alejandro.edu.drivesafeam.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()

    val context = LocalContext.current
    val prefManager = remember { PreferenciasManager(context) }
    val coroutineScope = rememberCoroutineScope()

    // ── Animaciones de entrada ──
    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        contentVisible = true
    }

    // ── Validaciones locales ──
    val passwordsMatch = contrasena == confirmar || confirmar.isEmpty()
    val passwordLengthOk = contrasena.length >= 6 || contrasena.isEmpty()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            val uid = authViewModel.getCurrentUserId() ?: ""
            prefManager.guardarIdUsuario(uid)
            val nombreParaGuardar = nombre.trim()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            if (nombreParaGuardar.isNotEmpty()) {
                prefManager.guardarNombreUsuario(nombreParaGuardar)
                context.getSharedPreferences("ConfigAutoSave", android.content.Context.MODE_PRIVATE)
                    .edit().putString("nombreUsuario", nombreParaGuardar).apply()
                
                // Sync to Firebase
                if (uid.isNotEmpty()) {
                    try {
                        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(uid)
                            .set(mapOf("nombre" to nombreParaGuardar, "correo" to correo), com.google.firebase.firestore.SetOptions.merge())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                val fallback = correo.substringBefore("@")
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                prefManager.guardarNombreUsuario(fallback)
                context.getSharedPreferences("ConfigAutoSave", android.content.Context.MODE_PRIVATE)
                    .edit().putString("nombreUsuario", fallback).apply()
                
                // Sync to Firebase
                if (uid.isNotEmpty()) {
                    try {
                        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(uid)
                            .set(mapOf("nombre" to fallback, "correo" to correo), com.google.firebase.firestore.SetOptions.merge())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            onRegisterSuccess()
            authViewModel.resetState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DriveSafeDark)
    ) {
        // ── Glow ambiental naranja sutil ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            DriveSafeOrange.copy(alpha = 0.05f),
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
            Spacer(modifier = Modifier.height(48.dp))

            // ── Header con logo ──
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
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Crear Cuenta",
                        style = MaterialTheme.typography.displayMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Únete a DriveSafe y viaja seguro",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextGray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ── Formulario ──
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(600, delayMillis = 200)) + slideInVertically(
                    initialOffsetY = { 30 },
                    animationSpec = tween(600, delayMillis = 200, easing = FastOutSlowInEasing)
                )
            ) {
                Column {
                    DriveSafeTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        placeholder = "Nombre completo",
                        leadingIcon = Icons.Outlined.Person,
                        isError = authState is AuthState.Error && nombre.isBlank()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    DriveSafeTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        placeholder = "Correo electrónico",
                        leadingIcon = Icons.Outlined.Email,
                        keyboardType = KeyboardType.Email,
                        isError = authState is AuthState.Error && correo.isBlank()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    DriveSafeTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it },
                        placeholder = "Contraseña",
                        isPassword = true,
                        leadingIcon = Icons.Outlined.Lock,
                        isError = !passwordLengthOk,
                        errorMessage = if (!passwordLengthOk) "Mínimo 6 caracteres" else null,
                        supportingText = if (contrasena.isEmpty()) "Mínimo 6 caracteres" else null
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    DriveSafeTextField(
                        value = confirmar,
                        onValueChange = { confirmar = it },
                        placeholder = "Confirmar contraseña",
                        isPassword = true,
                        leadingIcon = Icons.Outlined.Shield,
                        isError = !passwordsMatch,
                        errorMessage = if (!passwordsMatch) "Las contraseñas no coinciden" else null
                    )

                    // ── Error del servidor ──
                    AnimatedVisibility(
                        visible = authState is AuthState.Error,
                        enter = fadeIn(tween(200)) + expandVertically(),
                        exit = fadeOut(tween(200)) + shrinkVertically()
                    ) {
                        if (authState is AuthState.Error) {
                            Text(
                                text = (authState as AuthState.Error).message,
                                color = DriveSafeRed,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp, start = 4.dp),
                                textAlign = TextAlign.Start
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    DriveSafeButton(
                        text = "Crear Cuenta",
                        onClick = { authViewModel.register(correo, contrasena, confirmar) },
                        enabled = authState !is AuthState.Loading
                                && passwordsMatch
                                && passwordLengthOk
                                && nombre.isNotBlank()
                                && correo.isNotBlank()
                                && contrasena.isNotBlank()
                                && confirmar.isNotBlank(),
                        isLoading = authState is AuthState.Loading
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Link a Login ──
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(600, delayMillis = 400))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? ",
                        color = TextGray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Inicia Sesión",
                        color = DriveSafeOrange,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onNavigateToLogin() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
