package miguel.alejandro.edu.drivesafeam.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeButton
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeTextField
import miguel.alejandro.edu.drivesafeam.ui.viewmodel.AuthState
import miguel.alejandro.edu.drivesafeam.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onRegisterSuccess()
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
            text = "Crear Cuenta",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Únete a DriveSafe hoy",
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
        
        Spacer(modifier = Modifier.height(16.dp))

        DriveSafeTextField(
            value = confirmar,
            onValueChange = { confirmar = it },
            label = "Confirmar Contraseña",
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
                text = "Registrarse",
                onClick = { authViewModel.register(correo, contrasena, confirmar) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row {
            Text(
                text = "¿Ya tienes cuenta? ",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Inicia Sesión",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }
}
