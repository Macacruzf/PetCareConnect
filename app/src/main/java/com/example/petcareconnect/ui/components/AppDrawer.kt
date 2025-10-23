package com.example.petcareconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Estructura de un ítem de menú lateral
data class DrawerItem(
    val label: String, // Texto del ítem
    val icon: ImageVector, // Ícono a mostrar
    val onClick: () -> Unit // Acción al presionar
)

@Composable
fun AppDrawer(
    currentRoute: String?, // Ruta actual (para marcar seleccionado)
    items: List<DrawerItem>, // Lista de ítems del menú
    modifier: Modifier = Modifier // Modificador opcional
) {
    // Colores de la app
    val verde = Color(0xFF4CAF50)
    val grisClaro = Color(0xFFF5F5F5)
    val grisTexto = Color(0xFF333333)

    ModalDrawerSheet(
        modifier = modifier
            .background(grisClaro) // Fondo general del drawer
    ) {
        // Encabezado superior
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(verde)
                .padding(24.dp)
        ) {
            Text(
                text = "PetCare Connect",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Tienda Veterinaria",
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Opciones del menú
        items.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label, color = grisTexto) },
                selected = false, // Puedes reemplazar con (currentRoute == item.label.lowercase())
                onClick = item.onClick,
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        tint = verde // Íconos verdes
                    )
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = verde.copy(alpha = 0.15f),
                    unselectedContainerColor = Color.Transparent
                )
            )
        }

        // Pie de versión
        Divider(modifier = Modifier.padding(vertical = 12.dp))
        Text(
            text = "Versión 1.0.0",
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// Lista estándar de ítems del Drawer
@Composable
fun defaultDrawerItems(
    onHome: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit
): List<DrawerItem> = listOf(
    DrawerItem("Inicio", Icons.Filled.Home, onHome),
    DrawerItem("Iniciar sesión", Icons.Filled.AccountCircle, onLogin),
    DrawerItem("Registro", Icons.Filled.Person, onRegister)
)