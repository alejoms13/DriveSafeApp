package miguel.alejandro.edu.drivesafeam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import miguel.alejandro.edu.drivesafeam.data.model.ContactoEmergencia
import miguel.alejandro.edu.drivesafeam.data.local.PreferenciasManager
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeButton
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeCard
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeDisclaimer
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeIconButton
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeOutlineButton
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeTextField
import miguel.alejandro.edu.drivesafeam.ui.theme.*
import miguel.alejandro.edu.drivesafeam.ui.viewmodel.ConfiguracionViewModel
import miguel.alejandro.edu.drivesafeam.data.model.TipoAccionEmergencia

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    configViewModel: ConfiguracionViewModel = viewModel()
) {
    val configuracion by configViewModel.configuracion.collectAsState()
    val isLoggedOut by configViewModel.isLoggedOut.collectAsState()
    val contacto by configViewModel.contactoEmergencia.collectAsState()

    val context = LocalContext.current
    val prefManager = remember { PreferenciasManager(context) }
    val coroutineScope = rememberCoroutineScope()
    
    val nombreUsuarioDataStore by prefManager.nombreUsuario.collectAsState(initial = "")
    var nombreUsuarioLocal by remember(nombreUsuarioDataStore) { mutableStateOf(nombreUsuarioDataStore) }

    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut) {
            prefManager.limpiarSesion()
            onNavigateToLogin()
        }
    }

    val sharedPreferences = remember { context.getSharedPreferences("ConfigAutoSave", android.content.Context.MODE_PRIVATE) }
    LaunchedEffect(configuracion, contacto) {
        sharedPreferences.edit().apply {
            putBoolean("vibracionActiva", configuracion.vibracionActiva)
            putBoolean("sonidoActivo", configuracion.sonidoActivo)
            // Acción única de emergencia
            putString("accionEmergencia", configuracion.accionEmergencia.name)
            putInt("secondsBeforeAction", configuracion.secondsBeforeAction)
            putString("contactoNombre", contacto?.nombre ?: "")
            putString("contactoTelefono", contacto?.telefono ?: "")
            apply()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DriveSafeDark)
            .systemBarsPadding()
    ) {
        // ── Top Bar ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DriveSafeIconButton(
                icon = Icons.Default.ArrowBackIosNew ,
                onClick = onNavigateBack,
                contentDescription = "Regresar"
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Ajustes",
                style = MaterialTheme.typography.displaySmall,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ── Bloque 0: Perfil de Usuario ──
            Text(
                text = "Perfil de Usuario",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(12.dp))

            DriveSafeCard(surfaceColor = DriveSafeSurface1) {
                DriveSafeTextField(
                    value = nombreUsuarioLocal,
                    onValueChange = { nuevoNombre ->
                        nombreUsuarioLocal = nuevoNombre
                        sharedPreferences.edit().putString("nombreUsuario", nuevoNombre).apply()
                        coroutineScope.launch { prefManager.guardarNombreUsuario(nuevoNombre) }
                        configViewModel.updateNombreUsuario(nuevoNombre)
                    },
                    placeholder = "Tu nombre",
                    leadingIcon = Icons.Outlined.Person
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Bloque 1: Preferencias de Alerta ──
            Text(
                text = "Alertas y Notificaciones",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            DriveSafeCard(
                surfaceColor = DriveSafeSurface1
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Vibración Hápica", style = MaterialTheme.typography.bodyLarge, color = TextWhite)
                        Text("Vibra al detectar somnolencia", style = MaterialTheme.typography.bodySmall, color = TextGray)
                    }
                    Switch(
                        checked = configuracion.vibracionActiva,
                        onCheckedChange = { configViewModel.updateConfiguracion(configuracion.copy(vibracionActiva = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = DriveSafeOrange,
                            uncheckedThumbColor = TextGrayLight,
                            uncheckedTrackColor = DriveSafeSurface3
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = BorderGray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Sonido de Alarma", style = MaterialTheme.typography.bodyLarge, color = TextWhite)
                        Text("Alarma sonora en nivel crítico", style = MaterialTheme.typography.bodySmall, color = TextGray)
                    }
                    Switch(
                        checked = configuracion.sonidoActivo,
                        onCheckedChange = { configViewModel.updateConfiguracion(configuracion.copy(sonidoActivo = it)) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = DriveSafeOrange,
                            uncheckedThumbColor = TextGrayLight,
                            uncheckedTrackColor = DriveSafeSurface3
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Bloque 2: Contacto SOS ──
            Text(
                text = "Contacto de Emergencia (SOS)",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(12.dp))

            DriveSafeCard(surfaceColor = DriveSafeSurface1) {
                DriveSafeTextField(
                    value = contacto?.nombre ?: "",
                    onValueChange = { configViewModel.updateContacto(ContactoEmergencia(it, contacto?.telefono ?: "")) },
                    placeholder = "Nombre del Contacto"
                )
                Spacer(modifier = Modifier.height(12.dp))
                DriveSafeTextField(
                    value = contacto?.telefono ?: "",
                    onValueChange = { configViewModel.updateContacto(ContactoEmergencia(contacto?.nombre ?: "", it)) },
                    placeholder = "Número (Ej: +57300...)"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Bloque 3: Acción de Emergencia (Exclusión Mutua) ──
            Text(
                text = "Acción Automática de Emergencia",
                style = MaterialTheme.typography.titleMedium,
                color = TextWhite
            )
            Text(
                text = "Solo se ejecutará una acción cuando se detecte peligro prolongado.",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            DriveSafeCard(surfaceColor = DriveSafeSurface1) {
                // Opción 1: WhatsApp
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = configuracion.accionEmergencia == TipoAccionEmergencia.WHATSAPP,
                        onClick = { configViewModel.updateAccionEmergencia(TipoAccionEmergencia.WHATSAPP) },
                        colors = RadioButtonDefaults.colors(selectedColor = DriveSafeOrange)
                    )
                    Column {
                        Text("Enviar WhatsApp", style = MaterialTheme.typography.bodyLarge, color = TextWhite)
                        Text("Mensaje con ubicación GPS al contacto", style = MaterialTheme.typography.bodySmall, color = TextGray)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = BorderGray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // Opción 2: Llamada
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = configuracion.accionEmergencia == TipoAccionEmergencia.LLAMADA,
                        onClick = { configViewModel.updateAccionEmergencia(TipoAccionEmergencia.LLAMADA) },
                        colors = RadioButtonDefaults.colors(selectedColor = DriveSafeOrange)
                    )
                    Column {
                        Text("Llamada Directa", style = MaterialTheme.typography.bodyLarge, color = TextWhite)
                        Text("Llama automáticamente al número configurado", style = MaterialTheme.typography.bodySmall, color = TextGray)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = BorderGray, thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // Opción 3: Ninguna
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RadioButton(
                        selected = configuracion.accionEmergencia == TipoAccionEmergencia.NINGUNA,
                        onClick = { configViewModel.updateAccionEmergencia(TipoAccionEmergencia.NINGUNA) },
                        colors = RadioButtonDefaults.colors(selectedColor = DriveSafeOrange)
                    )
                    Column {
                        Text("Solo alertas locales", style = MaterialTheme.typography.bodyLarge, color = TextWhite)
                        Text("Sin contacto automático externo", style = MaterialTheme.typography.bodySmall, color = TextGray)
                    }
                }

                // ── Slider de tiempo único ──────────────────────────
                if (configuracion.accionEmergencia != TipoAccionEmergencia.NINGUNA) {
                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = BorderGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Retraso antes de ejecutar la acción",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextWhiteMedium
                    )
                    Slider(
                        value = configuracion.secondsBeforeAction.toFloat(),
                        onValueChange = {
                            configViewModel.updateConfiguracion(configuracion.copy(secondsBeforeAction = it.toInt()))
                        },
                        valueRange = 5f..60f,
                        colors = SliderDefaults.colors(thumbColor = DriveSafeOrange, activeTrackColor = DriveSafeOrange)
                    )
                    Text(
                        text = "${configuracion.secondsBeforeAction} segundos de peligro continuo",
                        style = MaterialTheme.typography.bodySmall,
                        color = DriveSafeOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            // ── Botones ──
            DriveSafeButton(
                text = "Guardar Cambios",
                onClick = { 
                    configViewModel.saveToFirebase()
                    android.widget.Toast.makeText(context, "Configuración guardada", android.widget.Toast.LENGTH_SHORT).show()
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            DriveSafeOutlineButton(
                text = "Cerrar Sesión",
                onClick = { configViewModel.logout() }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            DriveSafeDisclaimer()
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
