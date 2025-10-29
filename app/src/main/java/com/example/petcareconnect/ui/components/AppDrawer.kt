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

// Representa un elemento del menú lateral (Drawer)
data class DrawerItem(
    val label: String,          // Texto visible del ítem
    val icon: ImageVector,      // Ícono asociado al ítem
    val onClick: () -> Unit     // Acción que se ejecuta al seleccionarlo
)

@Composable
fun AppDrawer(
    currentRoute: String?,       // Ruta actual para identificar el ítem seleccionado
    items: List<DrawerItem>,     // Lista de opciones del menú
    modifier: Modifier = Modifier // Modificador opcional
) {
    // Definición de la paleta de colores usada en el Drawer
    val verde = Color(0xFF4CAF50)      // Verde corporativo
    val grisClaro = Color(0xFFF5F5F5)  // Fondo suave del Drawer
    val grisTexto = Color(0xFF333333)  // Color de texto de los ítems

    // Contenedor principal del menú lateral
    ModalDrawerSheet(
        modifier = modifier
            .background(grisClaro) // Fondo general del Drawer
    ) {
        // Sección superior o encabezado
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(verde)   // Fondo verde del encabezado
                .padding(24.dp)      // Espaciado interno
        ) {
            // Título principal (nombre de la aplicación)
            Text(
                text = "PetCare Connect",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            // Subtítulo o descripción
            Text(
                text = "Tienda Veterinaria",
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Cuerpo principal: lista de opciones del Drawer
        items.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label, color = grisTexto) }, // Texto del ítem
                selected = false, // Podría ser (currentRoute == item.label.lowercase()) para destacar la actual
                onClick = item.onClick, // Acción a ejecutar al presionar el ítem
                icon = {
                    Icon(
                        item.icon, // Ícono asociado al ítem
                        contentDescription = item.label,
                        tint = verde // Íconos verdes para coherencia visual
                    )
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = verde.copy(alpha = 0.15f), // Fondo animado al seleccionar
                    unselectedContainerColor = Color.Transparent
                )
            )
        }

        // Línea divisoria inferior antes del pie
        Divider(modifier = Modifier.padding(vertical = 12.dp))

        // Texto del pie del Drawer (información adicional, versión)
        Text(
            text = "Versión 1.0.0",
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// Función que retorna la lista predeterminada de ítems del Drawer
@Composable
fun defaultDrawerItems(
    onHome: () -> Unit,       // Acción al seleccionar “Inicio”
    onLogin: () -> Unit,      // Acción al seleccionar “Iniciar sesión”
    onRegister: () -> Unit    // Acción al seleccionar “Registro”
): List<DrawerItem> = listOf(
    DrawerItem("Inicio", Icons.Filled.Home, onHome),
    DrawerItem("Iniciar sesión", Icons.Filled.AccountCircle, onLogin),
    DrawerItem("Registro", Icons.Filled.Person, onRegister)
)
