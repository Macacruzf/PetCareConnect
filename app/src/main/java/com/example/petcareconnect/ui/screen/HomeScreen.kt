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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    rol: String?,
    usuarioNombre: String?,
    productos: List<Producto>,
    categorias: List<Categoria> = emptyList(), // ahora recibe las categorías
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onAgregarAlCarrito: (Producto) -> Unit,
    onGoCategorias: () -> Unit,
    onGoUsuarios: () -> Unit,
    onGoHistorial: () -> Unit,
    onAgregarProducto: () -> Unit,
    onGoCarrito: () -> Unit = {},
    onGoPedidos: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val fondo = Color(0xFFF5F5F5)
    val verde = Color(0xFF4CAF50)
    val azul = Color(0xFF2196F3)
    val morado = Color(0xFF6A1B9A)
    val gris = Color(0xFF607D8B)
    val naranja = Color(0xFFFF9800)

    val isAdmin = rol == "ADMIN"
    val isCliente = rol == "CLIENTE"
    val isInvitado = rol.isNullOrEmpty() || rol == "INVITADO"

    var selectedProducto by remember { mutableStateOf<Producto?>(null) }

    // Estado del filtro de categoría
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }
    var expandedFiltro by remember { mutableStateOf(false) }

    //  Filtrado dinámico
    val productosFiltrados = remember(productos, categoriaSeleccionada) {
        if (categoriaSeleccionada == "Todas") productos
        else productos.filter { p ->
            categorias.firstOrNull { it.idCategoria == p.categoriaId }?.nombre == categoriaSeleccionada
        }
    }

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
            // ---------- LOGO ----------
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

            when {
                isAdmin -> Text("Bienvenido $usuarioNombre 👑", color = Color(0xFF333333))
                isCliente -> Text("Hola $usuarioNombre 🐶", color = Color(0xFF333333))
                else -> Text("Tu tienda veterinaria en un solo lugar 🐾", color = Color(0xFF333333))
            }

            Spacer(Modifier.height(12.dp))

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
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFiltro) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expandedFiltro,
                    onDismissRequest = { expandedFiltro = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Todas") },
                        onClick = {
                            categoriaSeleccionada = "Todas"
                            expandedFiltro = false
                        }
                    )
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria.nombre) },
                            onClick = {
                                categoriaSeleccionada = categoria.nombre
                                expandedFiltro = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Productos disponibles",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = azul
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            // ---------- LISTADO DE PRODUCTOS ----------
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
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

            Spacer(Modifier.height(12.dp))

            // ---------- MENÚ ADMIN ----------
            if (isAdmin) {
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

            // ---------- MENÚ CLIENTE ----------
            if (isCliente) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = onGoCarrito, colors = ButtonDefaults.buttonColors(containerColor = naranja)) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Mi carrito")
                    }
                    Button(onClick = onGoPedidos, colors = ButtonDefaults.buttonColors(containerColor = azul)) {
                        Text("Mis pedidos")
                    }
                    OutlinedButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Cerrar sesión")
                    }
                }
            }

            // ---------- MENÚ INVITADO ----------
            if (isInvitado) {
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

        // ---------- DIÁLOGO DETALLE PRODUCTO ----------
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
