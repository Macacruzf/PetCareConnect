package com.example.petcareconnect.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    isLogged: Boolean,
    rol: String?,
    onOpenDrawer: () -> Unit,
    onHome: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onPerfil: () -> Unit,
    onCarrito: () -> Unit      // 👈 AÑADIDO AQUÍ
) {

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF4CAF50)
        ),
        title = {
            Text(
                text = "PetCare Connect",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )
        },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = "Menú",
                    tint = Color.White
                )
            }
        },
        actions = {

            // 🏠 Botón Home
            IconButton(onClick = onHome) {
                Icon(Icons.Filled.Home, contentDescription = "Inicio", tint = Color.White)
            }

            // 🛒 Carrito SIEMPRE visible
            IconButton(onClick = onCarrito) {
                Icon(
                    Icons.Filled.ShoppingCart,
                    contentDescription = "Carrito",
                    tint = Color.White
                )
            }

            // 🔐 Si NO está logueado → Login + Registro
            if (!isLogged) {
                IconButton(onClick = onLogin) {
                    Icon(Icons.Filled.AccountCircle, contentDescription = "Login", tint = Color.White)
                }

                IconButton(onClick = onRegister) {
                    Icon(Icons.Filled.Person, contentDescription = "Registro", tint = Color.White)
                }
            }

            // 👤 Si está logueado → Perfil
            if (isLogged) {
                IconButton(onClick = onPerfil) {
                    Icon(Icons.Filled.Person, contentDescription = "Perfil", tint = Color.White)
                }
            }
        }
    )
}
