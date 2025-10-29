package com.example.petcareconnect.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    onOpenDrawer: () -> Unit, // Acción que abre el menú lateral (Drawer)
    onHome: () -> Unit,       // Acción que redirige a la pantalla de inicio
    onLogin: () -> Unit,      // Acción que redirige a la pantalla de login
    onRegister: () -> Unit    // Acción que redirige a la pantalla de registro
) {
    // Variable de estado para controlar la visibilidad del menú desplegable (3 puntos)
    var showMenu by remember { mutableStateOf(false) }

    // Barra superior centrada, característica del diseño Material 3
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFF4CAF50),   // Color verde característico de PetCare
            titleContentColor = Color.White,      // Color del texto del título
            navigationIconContentColor = Color.White, // Color del ícono del menú lateral
            actionIconContentColor = Color.White  // Color de los íconos de acción (Home, Login, etc.)
        ),
        title = {
            // Título centrado en la barra
            Text(
                text = "PetCare Connect",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis // Evita que el texto se desborde
            )
        },
        navigationIcon = {
            // Botón que abre el menú lateral (Drawer)
            // Este botón activa una animación de deslizamiento al ejecutarse onOpenDrawer()
            IconButton(onClick = onOpenDrawer) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menú")
            }
        },
        actions = {
            // Íconos de acceso rápido a diferentes pantallas
            IconButton(onClick = onHome) {
                Icon(Icons.Filled.Home, contentDescription = "Inicio")
            }
            IconButton(onClick = onLogin) {
                Icon(Icons.Filled.AccountCircle, contentDescription = "Login")
            }
            IconButton(onClick = onRegister) {
                Icon(Icons.Filled.Person, contentDescription = "Registro")
            }

            // Botón del menú desplegable (ícono de tres puntos verticales)
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "Más opciones")
            }

            // Menú desplegable (DropdownMenu)
            DropdownMenu(
                expanded = showMenu,                // Controla si el menú está visible o no
                onDismissRequest = { showMenu = false }, // Cierra el menú al hacer clic fuera
                containerColor = Color.White        // Fondo blanco del menú
            ) {
                // Cada opción del menú realiza una acción y cierra el menú
                DropdownMenuItem(
                    text = { Text("Inicio") },
                    onClick = { showMenu = false; onHome() }
                )
                DropdownMenuItem(
                    text = { Text("Login") },
                    onClick = { showMenu = false; onLogin() }
                )
                DropdownMenuItem(
                    text = { Text("Registro") },
                    onClick = { showMenu = false; onRegister() }
                )
            }
        }
    )
}
