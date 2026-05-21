package miguel.alejandro.edu.drivesafeam


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Consejo1(
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val backgroundColor = Color(0xFF0D0D0D)
    val accentOrange = Color(0xFFFF6A00)

    Surface(
        modifier = modifier.fillMaxSize(),
        color = backgroundColor
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1A1A1A))
                )

                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = accentOrange,
                    modifier = Modifier.size(90.dp)
                )

                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(45.dp)
                        .offset(x = 45.dp, y = 35.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Unlock Features",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Learn about DriveSafe's key features and how they improve your driving experience.",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Row {

                Text("●", color = accentOrange, fontSize = 18.sp)

                Spacer(modifier = Modifier.width(8.dp))

                Text("○", color = Color.Gray, fontSize = 18.sp)

                Spacer(modifier = Modifier.width(8.dp))

                Text("○", color = Color.Gray, fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onContinueClick() },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),

                shape = RoundedCornerShape(28.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),

                contentPadding = PaddingValues()
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    accentOrange,
                                    Color(0xFFFF8C42)
                                )
                            ),
                            shape = RoundedCornerShape(28.dp)
                        ),

                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Continue",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Consejo1Preview() {

    Consejo1(
        onContinueClick = {}
    )
}
