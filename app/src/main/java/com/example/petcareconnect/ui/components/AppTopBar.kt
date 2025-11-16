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
    isLogged: Boolean,
    rol: String,
    onOpenDrawer: () -> Unit,
    onHome: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onPerfil: () -> Unit
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
                Icon(Icons.Filled.Menu, contentDescription = "Menú", tint = Color.White)
            }
        },
        actions = {
            IconButton(onClick = onHome) {
                Icon(Icons.Filled.Home, contentDescription = "Inicio", tint = Color.White)
            }

            if (!isLogged) {
                IconButton(onClick = onLogin) {
                    Icon(Icons.Filled.AccountCircle, contentDescription = "Login", tint = Color.White)
                }
                IconButton(onClick = onRegister) {
                    Icon(Icons.Filled.Person, contentDescription = "Registro", tint = Color.White)
                }
            }

            if (isLogged) {
                IconButton(onClick = onPerfil) {
                    Icon(Icons.Filled.Person, contentDescription = "Perfil", tint = Color.White)
                }
            }
        }
    )
}
