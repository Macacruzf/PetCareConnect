package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.petcareconnect.data.model.Categoria

/*
 * Pantalla principal (Home) de PetCare Connect.
 * Muestra los productos disponibles, un filtro por categoría
 * y menús diferenciados según el tipo de usuario (admin, cliente o invitado).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    rol: String?,                                // Rol del usuario actual (ADMIN, CLIENTE, INVITADO)
    usuarioNombre: String?,                      // Nombre del usuario conectado
    productos: List<Producto>,                   // Lista de productos cargados
    categorias: List<Categoria> = emptyList(),   // Lista de categorías para el filtro
    onGoLogin: () -> Unit,                       // Navega a la pantalla de login
    onGoRegister: () -> Unit,                    // Navega al registro
    onAgregarAlCarrito: (Producto) -> Unit,      // Acción al agregar un producto al carrito
    onGoCategorias: () -> Unit,                  // Navega a gestión de categorías (admin)
    onGoUsuarios: () -> Unit,                    // Navega a gestión de usuarios (admin)
    onGoHistorial: () -> Unit,                   // Navega al historial de ventas (admin)
    onAgregarProducto: () -> Unit,               // Acción para agregar un nuevo producto
    onGoCarrito: () -> Unit = {},                // Navega al carrito (cliente)
    onGoPedidos: () -> Unit = {},                // Navega al historial de pedidos (cliente)
    onLogout: () -> Unit = {}                    // Cierra sesión
) {
    // Paleta de colores personalizada
    val fondo = Color(0xFFF5F5F5)
    val verde = Color(0xFF4CAF50)
    val azul = Color(0xFF2196F3)
    val morado = Color(0xFF6A1B9A)
    val gris = Color(0xFF607D8B)
    val naranja = Color(0xFFFF9800)

    // Determinación de rol del usuario
    val isAdmin = rol == "ADMIN"
    val isCliente = rol == "CLIENTE"
    val isInvitado = rol.isNullOrEmpty() || rol == "INVITADO"

    // Estado de selección y filtrado de productos
    var selectedProducto by remember { mutableStateOf<Producto?>(null) }
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }
    var expandedFiltro by remember { mutableStateOf(false) }

    // Filtrado dinámico de productos según la categoría seleccionada
    val productosFiltrados = remember(productos, categoriaSeleccionada) {
        if (categoriaSeleccionada == "Todas") productos
        else productos.filter { p ->
            categorias.firstOrNull { it.idCategoria == p.categoriaId }?.nombre == categoriaSeleccionada
        }
    }

    // Contenedor principal de toda la vista
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fondo)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // ---------- ENCABEZADO ----------
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                // Logo de la aplicación
                Image(
                    painter = painterResource(id = R.drawable.ic_petcare_logo),
                    contentDescription = "Logo PetCare Connect",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                // Título y saludo
                Column {
                    Text(
                        text = "PetCare Connect",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = verde
                        )
                    )
                    when {
                        isAdmin -> Text("Bienvenido $usuarioNombre", color = Color(0xFF333333))
                        isCliente -> Text("Hola $usuarioNombre", color = Color(0xFF333333))
                        else -> Text("Tu tienda veterinaria", color = Color(0xFF333333))
                    }
                }
            }

            // ---------- FILTRO DE CATEGORÍAS ----------
            ExposedDropdownMenuBox(
                expanded = expandedFiltro,
                onExpandedChange = { expandedFiltro = !expandedFiltro }
            ) {
                OutlinedTextField(
                    value = categoriaSeleccionada,
                    onValueChange = {},
                    label = { Text("Filtrar por categoría") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFiltro)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedFiltro,
                    onDismissRequest = { expandedFiltro = false }
                ) {
                    DropdownMenuItem(text = { Text("Todas") }, onClick = {
                        categoriaSeleccionada = "Todas"
                        expandedFiltro = false
                    })
                    categorias.forEach { categoria ->
                        DropdownMenuItem(text = { Text(categoria.nombre) }, onClick = {
                            categoriaSeleccionada = categoria.nombre
                            expandedFiltro = false
                        })
                    }
                }
            }

            // ---------- LISTADO DE PRODUCTOS ----------
            Text(
                text = "Productos disponibles",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = azul
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            )

            // Lista desplazable con los productos filtrados
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(productosFiltrados) { producto ->
                    ProductoCard(
                        producto = producto,
                        isAdmin = isAdmin,
                        isCliente = isCliente,
                        onClick = { selectedProducto = producto },
                        onDelete = {},
                        onAgregarAlCarrito = { onAgregarAlCarrito(producto) }
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // ---------- MENÚ ADMINISTRADOR ----------
            if (isAdmin) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
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

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = onGoPedidos, colors = ButtonDefaults.buttonColors(containerColor = azul)) {
                            Text("Ver pedidos", color = Color.White)
                        }
                        OutlinedButton(
                            onClick = onLogout,
                            modifier = Modifier.height(42.dp).width(160.dp)
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Cerrar sesión")
                        }
                    }

                    // Botón flotante para agregar productos (solo admin)
                    ExtendedFloatingActionButton(
                        onClick = onAgregarProducto,
                        icon = { Icon(Icons.Default.Add, contentDescription = "Agregar producto") },
                        text = { Text("Nuevo producto") },
                        containerColor = verde
                    )
                }
            }

            // ---------- MENÚ CLIENTE ----------
            if (isCliente) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onGoCarrito,
                        colors = ButtonDefaults.buttonColors(containerColor = naranja)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Mi carrito")
                    }
                    Button(onClick = onGoPedidos, colors = ButtonDefaults.buttonColors(containerColor = azul)) {
                        Text("Mis pedidos")
                    }
                }

                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.height(42.dp).width(160.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF444444))
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Cerrar sesión")
                }
            }

            // ---------- MENÚ INVITADO ----------
            if (isInvitado) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onGoLogin,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = verde)
                    ) {
                        Text("Iniciar sesión")
                    }
                    Button(
                        onClick = onGoRegister,
                        colors = ButtonDefaults.buttonColors(containerColor = naranja)
                    ) {
                        Text("Registrarse", color = Color.White)
                    }
                }
            }

            // Pie de página con información general
            Text(
                text = "© PetCare Connect 2025\nSolo retiro en tienda",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = azul,
                    textAlign = TextAlign.Center
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // ---------- DETALLE DE PRODUCTO ----------
        if (selectedProducto != null) {
            ProductoDetalleDialog(
                producto = selectedProducto!!,
                onAgregar = {
                    onAgregarAlCarrito(it)
                    selectedProducto = null
                },
                onCerrar = { selectedProducto = null },
                esAdmin = isAdmin
            )
        }
    }
}
