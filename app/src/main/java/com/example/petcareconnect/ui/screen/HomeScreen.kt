package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.petcareconnect.R

@Composable
fun HomeScreen(
    rol: String?, // "ADMIN", "CLIENTE" o null si no hay sesión
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onGoProductos: () -> Unit,
    onGoCategorias: () -> Unit,
    onGoHistorial: () -> Unit,
    onGoUsuarios: () -> Unit
) {
    val fondo = Color(0xFFF5F5F5)
    val verde = Color(0xFF4CAF50)
    val azul = Color(0xFF2196F3)
    val morado = Color(0xFF6A1B9A)
    val cian = Color(0xFF009688)
    val naranja = Color(0xFFFF9800)
    val grisTexto = Color(0xFF333333)
    val gris = Color(0xFF607D8B)

    val isAdmin = rol == "ADMIN"
    val isCliente = rol == "CLIENTE"
    val noSesion = rol == null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fondo)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            // ---------- LOGO Y TÍTULO ----------
            Image(
                painter = painterResource(id = R.drawable.ic_petcare_logo),
                contentDescription = "Logo PetCare Connect",
                modifier = Modifier.size(100.dp),
                colorFilter = ColorFilter.tint(verde)
            )

            Text(
                text = "PetCare Connect",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = verde
                )
            )
            Text(
                text = "Tu tienda veterinaria en un solo lugar",
                style = MaterialTheme.typography.titleMedium.copy(color = grisTexto),
                textAlign = TextAlign.Center
            )

            // ---------- BOTONES CIRCULARES ----------
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RoundButton("Productos", R.drawable.ic_petcare_logo, azul, onGoProductos)
                    if (isAdmin) {
                        RoundButton("Categorías", R.drawable.ic_petcare_logo, cian, onGoCategorias)
                    }
                }

                if (isAdmin) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RoundButton("Historial", R.drawable.ic_petcare_logo, gris, onGoHistorial)
                        RoundButton("Usuarios", R.drawable.ic_petcare_logo, morado, onGoUsuarios)
                    }
                }
            }

            // ---------- ACCESO / REGISTRO ----------
            if (noSesion) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    OutlinedButton(
                        onClick = onGoLogin,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = verde)
                    ) { Text("Iniciar sesión") }

                    Button(
                        onClick = onGoRegister,
                        colors = ButtonDefaults.buttonColors(containerColor = naranja)
                    ) { Text("Registrarse", color = Color.White) }
                }
            }

            // ---------- PIE ----------
            Text(
                text = "© PetCare Connect 2025\nSolo retiro en tienda",
                style = MaterialTheme.typography.bodySmall.copy(color = azul, textAlign = TextAlign.Center),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

// ---------- COMPONENTE BOTÓN CIRCULAR ----------
@Composable
fun RoundButton(
    label: String,
    iconRes: Int,
    color: Color,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onClick,
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = color),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(label, color = Color.Black, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
    }
}
