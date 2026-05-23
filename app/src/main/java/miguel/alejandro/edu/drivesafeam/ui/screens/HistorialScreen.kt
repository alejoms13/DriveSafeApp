package miguel.alejandro.edu.drivesafeam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeCard
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeIconButton
import miguel.alejandro.edu.drivesafeam.ui.theme.*
import miguel.alejandro.edu.drivesafeam.ui.viewmodel.HistorialViewModel
import miguel.alejandro.edu.drivesafeam.data.model.NivelAlerta
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistorialScreen(
    onNavigateBack: () -> Unit,
    historialViewModel: HistorialViewModel = viewModel()
) {
    val alertas by historialViewModel.alertas.collectAsState()
    val isLoading by historialViewModel.isLoading.collectAsState()

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
                icon = Icons.Default.ArrowBack,
                onClick = onNavigateBack,
                contentDescription = "Regresar"
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Historial",
                style = MaterialTheme.typography.displaySmall,
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DriveSafeOrange)
            }
        } else if (alertas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "No hay registros",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tus alertas de conducción aparecerán aquí",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(alertas) { alerta ->
                    val colorNivel = when (alerta.nivel) {
                        NivelAlerta.ADVERTENCIA -> DriveSafeYellow
                        NivelAlerta.PELIGRO, NivelAlerta.CRITICO -> DriveSafeRed
                        else -> TextGray
                    }
                    
                    val date = Date(alerta.timestamp)
                    val formatTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val formatDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

                    DriveSafeCard(
                        modifier = Modifier.padding(vertical = 8.dp),
                        surfaceColor = DriveSafeSurface1,
                        borderColor = BorderSubtle
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Icono de estado
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(colorNivel)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            // Detalle
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = alerta.tipo.name,
                                    color = TextWhite,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${formatDate.format(date)} • ${alerta.nivel.name}",
                                    color = TextGray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            // Hora
                            Text(
                                text = formatTime.format(date),
                                color = TextWhiteMedium,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (alerta.latitud != null && alerta.longitud != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = BorderGray, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "📍 Ubicación registrada",
                                color = TextGrayLight,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}
