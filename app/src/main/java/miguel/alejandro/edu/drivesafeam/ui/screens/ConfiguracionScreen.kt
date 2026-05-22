package miguel.alejandro.edu.drivesafeam.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeButton
import miguel.alejandro.edu.drivesafeam.ui.viewmodel.ConfiguracionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    configViewModel: ConfiguracionViewModel = viewModel()
) {
    val configuracion by configViewModel.configuracion.collectAsState()
    val isLoggedOut by configViewModel.isLoggedOut.collectAsState()

    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut) {
            onNavigateToLogin()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Opciones de Alerta", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Vibración", modifier = Modifier.weight(1f))
                Switch(
                    checked = configuracion.vibracionActiva,
                    onCheckedChange = { configViewModel.updateConfiguracion(configuracion.copy(vibracionActiva = it)) }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Sonido", modifier = Modifier.weight(1f))
                Switch(
                    checked = configuracion.sonidoActivo,
                    onCheckedChange = { configViewModel.updateConfiguracion(configuracion.copy(sonidoActivo = it)) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            DriveSafeButton(
                text = "Cerrar Sesión",
                onClick = { configViewModel.logout() },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "DriveSafe es una herramienta de asistencia. El conductor es el único responsable de mantener la atención en la vía en todo momento.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
