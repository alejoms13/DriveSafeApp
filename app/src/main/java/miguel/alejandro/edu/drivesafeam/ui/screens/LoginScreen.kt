package miguel.alejandro.edu.drivesafeam.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import miguel.alejandro.edu.drivesafeam.data.local.PreferenciasManager
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeButton
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeTextField
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
    val scope = rememberCoroutineScope()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            // Guardar algo en preferencias si es necesario
            onLoginSuccess()
            authViewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bienvenido a DriveSafe",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Inicia sesión para continuar",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        DriveSafeTextField(
            value = correo,
            onValueChange = { correo = it },
            label = "Correo Electrónico"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        DriveSafeTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = "Contraseña",
            visualTransformation = PasswordVisualTransformation()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (authState is AuthState.Error) {
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        if (authState is AuthState.Loading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            DriveSafeButton(
                text = "Iniciar Sesión",
                onClick = { authViewModel.login(correo, contrasena) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row {
            Text(
                text = "¿No tienes una cuenta? ",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Regístrate",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}
