package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.petcareconnect.R
import com.example.petcareconnect.data.model.Producto
import androidx.compose.material.icons.filled.Add


@Composable
fun HomeScreen(
    rol: String?, // "ADMIN", "CLIENTE" o null si no hay sesión
    productos: List<Producto>, // 🔹 Lista de productos recibida desde el ViewModel
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onAgregarAlCarrito: (Producto) -> Unit, // 🔹 Acción de compra para cliente/invitado
    onGoCategorias: () -> Unit,
    onGoUsuarios: () -> Unit,
    onGoHistorial: () -> Unit,
    onAgregarProducto: () -> Unit // 🔹 Solo admin
) {
    val fondo = Color(0xFFF5F5F5)
    val verde = Color(0xFF4CAF50)
    val azul = Color(0xFF2196F3)
    val morado = Color(0xFF6A1B9A)
    val gris = Color(0xFF607D8B)
    val naranja = Color(0xFFFF9800)

    val isAdmin = rol == "ADMIN"
    val isCliente = rol == "CLIENTE" || rol == "INVITADO"
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // ---------- LOGO Y TÍTULO ----------
            Image(
                painter = painterResource(id = R.drawable.ic_petcare_logo),
                contentDescription = "Logo PetCare Connect",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Text(
                text = "PetCare Connect",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = verde
                )
            )
            Text(
                text = "Tu tienda veterinaria en un solo lugar 🐾",
                style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF333333)),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            // ---------- LISTADO DE PRODUCTOS ----------
            Text(
                text = "Productos disponibles",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = azul
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(productos) { producto ->
                    ProductoCard(
                        producto = producto,
                        isAdmin = isAdmin,
                        isCliente = isCliente,
                        onDelete = {}, // ❌ No se usa en HomeScreen
                        onAgregarAlCarrito = { onAgregarAlCarrito(producto) }
                    )
                }
            }

            // ---------- BOTONES SOLO ADMIN ----------
            if (isAdmin) {
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = onGoCategorias, colors = ButtonDefaults.buttonColors(containerColor = azul)) {
                        Text("Categorías")
                    }
                    Button(onClick = onGoUsuarios, colors = ButtonDefaults.buttonColors(containerColor = morado)) {
                        Text("Usuarios")
                    }
                    Button(onClick = onGoHistorial, colors = ButtonDefaults.buttonColors(containerColor = gris)) {
                        Text("Historial")
                    }
                }

                Spacer(Modifier.height(12.dp))
                ExtendedFloatingActionButton(
                    onClick = onAgregarProducto,
                    icon = { Icon(Icons.Default.Add, contentDescription = "Agregar producto") },
                    text = { Text("Nuevo producto") },
                    containerColor = verde
                )
            }

            // ---------- ACCESO / REGISTRO ----------
            if (noSesion) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                style = MaterialTheme.typography.bodySmall.copy(
                    color = azul,
                    textAlign = TextAlign.Center
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}


