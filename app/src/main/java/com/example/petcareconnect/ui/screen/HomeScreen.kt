package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.navigation.NavController
import com.example.petcareconnect.R
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.model.Categoria
import com.example.petcareconnect.data.model.EstadoProducto

// PALETA PETCARE
val VerdePC = Color(0xFF4CAF50)
val AzulPC = Color(0xFF2196F3)
val NaranjaPC = Color(0xFFFF9800)
val GrisPC = Color(0xFF607D8B)
val FondoPC = Color(0xFFF5F5F5)

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

    onCambiarEstado: (Producto) -> Unit = {},
    onEliminar: (Producto) -> Unit = {},

    // EDITAR (corregido)
    onEditar: (Producto) -> Unit
) {
    val isAdmin = rol == "ADMIN"
    val isCliente = rol == "CLIENTE"
    val isInvitado = rol.isNullOrEmpty() || rol == "INVITADO"

    var selectedProducto by remember { mutableStateOf<Producto?>(null) }
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }
    var expandedFiltro by remember { mutableStateOf(false) }

    // FILTRO DE CATEGORÍA
    val productosFiltrados = remember(productos, categoriaSeleccionada) {
        if (categoriaSeleccionada == "Todas") productos
        else productos.filter { p ->
            categorias.firstOrNull { it.idCategoria == p.categoriaId }?.nombre == categoriaSeleccionada
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPC)
            .padding(16.dp)
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // HEADER LOGO + NOMBRE
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_petcare_logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        "PetCare Connect",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = VerdePC
                        )
                    )
                    when {
                        isAdmin -> Text("Bienvenido $usuarioNombre")
                        isCliente -> Text("Hola $usuarioNombre")
                        else -> Text("Tu tienda veterinaria")
                    }
                }
            }

            Spacer(Modifier.height(15.dp))

            // FILTRO
            ExposedDropdownMenuBox(
                expanded = expandedFiltro,
                onExpandedChange = { expandedFiltro = !expandedFiltro }
            ) {
                OutlinedTextField(
                    value = categoriaSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filtrar por categoría") },
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

            Spacer(Modifier.height(10.dp))

            Text(
                "Productos disponibles",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AzulPC
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            // LISTA
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(productosFiltrados) { p ->

                    ProductoCard(
                        producto = p,
                        isAdmin = isAdmin,
                        isCliente = isCliente,

                        onClick = { selectedProducto = p },

                        onEditar = { onEditar(p) }, // ← EDITAR CORRECTO

                        onCambiarEstado = { est ->
                            onCambiarEstado(p.copy(estado = est))
                        },

                        onDelete = { onEliminar(p) },

                        onAgregarAlCarrito = { onAgregarAlCarrito(p) }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ADMIN
            if (isAdmin) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = onGoCategorias, colors = ButtonDefaults.buttonColors(AzulPC)) {
                            Text("Categorías")
                        }
                        Button(onClick = onGoUsuarios, colors = ButtonDefaults.buttonColors(NaranjaPC)) {
                            Text("Usuarios")
                        }
                        Button(onClick = onGoHistorial, colors = ButtonDefaults.buttonColors(GrisPC)) {
                            Text("Historial")
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = onGoPedidos, colors = ButtonDefaults.buttonColors(AzulPC)) {
                            Text("Ver pedidos")
                        }
                        OutlinedButton(onClick = onLogout) {
                            Icon(Icons.Default.Logout, null)
                            Spacer(Modifier.width(4.dp))
                            Text("Cerrar sesión")
                        }
                    }

                    ExtendedFloatingActionButton(
                        onClick = onAgregarProducto,
                        icon = { Icon(Icons.Default.Add, null) },
                        text = { Text("Nuevo producto") },
                        containerColor = VerdePC
                    )
                }
            }

            // CLIENTE
            if (isCliente) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = onGoCarrito, colors = ButtonDefaults.buttonColors(NaranjaPC)) {
                        Icon(Icons.Default.ShoppingCart, null)
                        Spacer(Modifier.width(4.dp))
                        Text("Mi carrito")
                    }
                    Button(onClick = onGoPedidos, colors = ButtonDefaults.buttonColors(AzulPC)) {
                        Text("Mis pedidos")
                    }
                }

                Spacer(Modifier.height(6.dp))
                OutlinedButton(onClick = onLogout) {
                    Icon(Icons.Default.Logout, null)
                    Spacer(Modifier.width(4.dp))
                    Text("Cerrar sesión")
                }
            }

            // INVITADO
            if (isInvitado) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onGoLogin) {
                        Text("Iniciar sesión")
                    }
                    Button(
                        onClick = onGoRegister,
                        colors = ButtonDefaults.buttonColors(NaranjaPC)
                    ) {
                        Text("Registrarse", color = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Text(
                "© PetCare Connect 2025\nSolo retiro en tienda",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = AzulPC,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        // DETALLE DEL PRODUCTO
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

                onCerrar = { selectedProducto = null },

                onEditar = {
                    onEditar(selectedProducto!!)
                    selectedProducto = null
                }
            )
        }
    }
}
