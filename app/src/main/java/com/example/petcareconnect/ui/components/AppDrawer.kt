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

// -----------------------------
// MODELO PARA EL ÍTEM DEL DRAWER
// -----------------------------
data class DrawerItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

// -----------------------------
//      DRAWER COMPLETO
// -----------------------------
@Composable
fun AppDrawer(
    currentRoute: String?,
    items: List<DrawerItem>,
    modifier: Modifier = Modifier
) {
    val verde = Color(0xFF4CAF50)
    val grisClaro = Color(0xFFF5F5F5)
    val grisTexto = Color(0xFF333333)

    ModalDrawerSheet(
        modifier = modifier.background(grisClaro)
    ) {

        // Encabezado
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

        // Items de navegación
        items.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label, color = grisTexto) },
                selected = false,
                onClick = item.onClick,
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        tint = verde
                    )
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }

        Divider(modifier = Modifier.padding(vertical = 12.dp))

        Text(
            "Versión 1.0.0",
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// -----------------------------
//   LISTA DINÁMICA DE OPCIONES
// -----------------------------
@Composable
fun defaultDrawerItems(
    userRole: String?,
    isLoggedIn: Boolean,
    onHome: () -> Unit,
    onProductos: () -> Unit,
    onCategorias: () -> Unit,
    onUsuarios: () -> Unit,
    onCarrito: () -> Unit,
    onPedidos: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onLogout: () -> Unit
): List<DrawerItem> {

    val items = mutableListOf<DrawerItem>()

    // ⭐ SIEMPRE DISPONIBLE
    items.add(DrawerItem("Inicio", Icons.Filled.Home, onHome))

    // -----------------
    // INVITADO
    // -----------------
    if (!isLoggedIn) {

        // ⭐ NUEVO: Productos para invitados
        items.add(DrawerItem("Productos", Icons.Filled.ShoppingBag, onProductos))

        items.add(DrawerItem("Iniciar sesión", Icons.Filled.AccountCircle, onLogin))
        items.add(DrawerItem("Registrar", Icons.Filled.PersonAdd, onRegister))
        return items
    }

    // -----------------
    // CLIENTE
    // -----------------
    if (userRole == "CLIENTE") {

        // ⭐ NUEVO: Productos para clientes
        items.add(DrawerItem("Productos", Icons.Filled.ShoppingBag, onProductos))

        items.add(DrawerItem("Mi carrito", Icons.Filled.ShoppingCart, onCarrito))
        items.add(DrawerItem("Mis pedidos", Icons.Filled.ShoppingBag, onPedidos))
    }

    // -----------------
    // ADMIN
    // -----------------
    if (userRole == "ADMIN") {

        // Panel completo
        items.add(DrawerItem("Productos", Icons.Filled.Pets, onProductos))
        items.add(DrawerItem("Categorías", Icons.Filled.Category, onCategorias))
        items.add(DrawerItem("Usuarios", Icons.Filled.Group, onUsuarios))

        items.add(DrawerItem("Ver pedidos", Icons.Filled.LocalShipping, onPedidos))

    }

    // -----------------
    // CERRAR SESIÓN
    // -----------------
    items.add(DrawerItem("Cerrar sesión", Icons.Filled.Logout, onLogout))

    return items
}
