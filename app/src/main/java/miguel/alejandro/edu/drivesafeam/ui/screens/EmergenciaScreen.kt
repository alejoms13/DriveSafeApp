package miguel.alejandro.edu.drivesafeam.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import miguel.alejandro.edu.drivesafeam.ui.components.DriveSafeButton

@Composable
fun EmergenciaScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFB71C1C))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "¡ALERTA CRÍTICA!",
            style = MaterialTheme.typography.displayMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Se ha detectado una distracción o somnolencia prolongada.",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(64.dp))
        
        DriveSafeButton(
            text = "ESTOY BIEN",
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth().height(60.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        DriveSafeButton(
            text = "AVISAR POR WHATSAPP",
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW)
                val message = "Alerta de DriveSafe: Necesito ayuda en mi ubicación actual."
                intent.data = Uri.parse("https://wa.me/?text=${Uri.encode(message)}")
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth().height(60.dp)
        )
    }
}
