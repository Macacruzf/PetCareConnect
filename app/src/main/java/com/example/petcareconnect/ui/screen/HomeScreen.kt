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
import com.example.petcareconnect.data.model.EstadoProducto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    rol: String?,
    usuarioNombre: String?,
    productos: List<Producto>,
    categorias: List<Categoria> = emptyList(),

    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onAgregarAlCarrito: (Producto) -> Unit,
    onGoCategorias: () -> Unit,
    onGoUsuarios: () -> Unit,
    onGoHistorial: () -> Unit,
    onAgregarProducto: () -> Unit,
    onGoCarrito: () -> Unit = {},
    onGoPedidos: () -> Unit = {},
    onLogout: () -> Unit = {},

    // NUEVOS
    onCambiarEstado: (Producto) -> Unit = {},
    onEliminar: (Producto) -> Unit = {}
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
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }
    var expandedFiltro by remember { mutableStateOf(false) }

    // Filtrado por categoría
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
            .padding(16.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // ---------------- ENCABEZADO ----------------
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_petcare_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(70.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Column {
                    Text(
                        "PetCare Connect",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = verde
                        )
                    )
                    when {
                        isAdmin -> Text("Bienvenido $usuarioNombre")
                        isCliente -> Text("Hola $usuarioNombre")
                        else -> Text("Tu tienda veterinaria")
                    }
                }
            }

            // ---------------- FILTRO ----------------
            ExposedDropdownMenuBox(
                expanded = expandedFiltro,
                onExpandedChange = { expandedFiltro = !expandedFiltro }
            ) {
                OutlinedTextField(
                    value = categoriaSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filtrar por categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFiltro) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
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

            Text(
                "Productos disponibles",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = azul
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // ---------------- LISTA ----------------
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(productosFiltrados) { producto ->

                    ProductoCard(
                        producto = producto,
                        isAdmin = isAdmin,
                        isCliente = isCliente,

                        onClick = { selectedProducto = producto },

                        onCambiarEstado = { estado ->
                            onCambiarEstado(producto.copy(estado = estado))
                        },

                        onDelete = { onEliminar(producto) },

                        onAgregarAlCarrito = { onAgregarAlCarrito(producto) }
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // ---------------- ADMIN ----------------
            if (isAdmin) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = onGoCategorias, colors = ButtonDefaults.buttonColors(azul)) {
                            Text("Categorías")
                        }
                        Button(onClick = onGoUsuarios, colors = ButtonDefaults.buttonColors(morado)) {
                            Text("Usuarios")
                        }
                        Button(onClick = onGoHistorial, colors = ButtonDefaults.buttonColors(gris)) {
                            Text("Historial")
                        }
                    }

                    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = onGoPedidos, colors = ButtonDefaults.buttonColors(azul)) {
                            Text("Ver pedidos")
                        }
                        OutlinedButton(onClick = onLogout, modifier = Modifier.height(42.dp).width(160.dp)) {
                            Icon(Icons.Default.Logout, null)
                            Spacer(Modifier.width(4.dp))
                            Text("Cerrar sesión")
                        }
                    }

                    ExtendedFloatingActionButton(
                        onClick = onAgregarProducto,
                        icon = { Icon(Icons.Default.Add, null) },
                        text = { Text("Nuevo producto") },
                        containerColor = verde
                    )
                }
            }

            // ---------------- CLIENTE ----------------
            if (isCliente) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = onGoCarrito, colors = ButtonDefaults.buttonColors(naranja)) {
                        Icon(Icons.Default.ShoppingCart, null)
                        Spacer(Modifier.width(4.dp))
                        Text("Mi carrito")
                    }
                    Button(onClick = onGoPedidos, colors = ButtonDefaults.buttonColors(azul)) {
                        Text("Mis pedidos")
                    }
                }

                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.height(42.dp).width(160.dp)
                ) {
                    Icon(Icons.Default.Logout, null)
                    Spacer(Modifier.width(4.dp))
                    Text("Cerrar sesión")
                }
            }

            // ---------------- INVITADO ----------------
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
                "© PetCare Connect 2025\nSolo retiro en tienda",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = azul,
                    textAlign = TextAlign.Center
                ),
                textAlign = TextAlign.Center
            )
        }

        // ---------------- DETALLE ----------------
        if (selectedProducto != null) {
            ProductoDetalleDialog(
                producto = selectedProducto!!,
                esAdmin = isAdmin,

                onCambiarEstado = { estado ->
                    onCambiarEstado(selectedProducto!!.copy(estado = estado))
                },

                onAgregar = {
                    onAgregarAlCarrito(it)
                    selectedProducto = null
                },

                onEliminar = {
                    onEliminar(selectedProducto!!)
                    selectedProducto = null
                },

                onCerrar = { selectedProducto = null }
            )
        }
    }
}
