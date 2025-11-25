package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.petcareconnect.R
import com.example.petcareconnect.data.model.Categoria
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.remote.repository.TicketRemoteRepository
import com.example.petcareconnect.ui.viewmodel.TicketViewModel
import com.example.petcareconnect.ui.viewmodel.TicketViewModelFactory

val VerdePC = Color(0xFF4CAF50)
val AzulPC = Color(0xFF2196F3)
val NaranjaPC = Color(0xFFFF9800)
val GrisPC = Color(0xFF607D8B)
val FondoPC = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    rol: String?,
    usuarioNombre: String?,
    usuarioId: Long,    // ⭐ AHORA viene desde afuera
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
    onEditar: (Producto) -> Unit = {}
) {

    val isAdmin = rol == "ADMIN"
    val isCliente = rol == "CLIENTE"

    var selectedProducto by remember { mutableStateOf<Producto?>(null) }

    var categoriaSeleccionada by remember { mutableStateOf("Todas") }
    var expandedFiltro by remember { mutableStateOf(false) }

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

            // LOGO + SALUDO
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_petcare_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(70.dp).clip(CircleShape),
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

                    categorias.forEach {
                        DropdownMenuItem(
                            text = { Text(it.nombre) },
                            onClick = {
                                categoriaSeleccionada = it.nombre
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
                        onEditar = { onEditar(p) },
                        onCambiarEstado = { est ->
                            onCambiarEstado(p.copy(estado = est))
                        },
                        onDelete = { onEliminar(p) },
                        onAgregarAlCarrito = { onAgregarAlCarrito(p) }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (isAdmin) {
                ExtendedFloatingActionButton(
                    onClick = onAgregarProducto,
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Nuevo producto") },
                    containerColor = VerdePC
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                "© PetCare Connect 2025\nSolo retiro en tienda",
                style = MaterialTheme.typography.bodySmall.copy(color = AzulPC),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        // ============================================================
        //                   ⭐ DIALOGO DETALLE PRODUCTO ⭐
        // ============================================================
        if (selectedProducto != null) {

            // ViewModel de tickets
            val vmTicket: TicketViewModel = viewModel(
                factory = TicketViewModelFactory(TicketRemoteRepository())
            )

            ProductoDetalleDialog(
                producto = selectedProducto!!,
                usuarioId = usuarioId,          // <<<<<<<<<< AQUI VA TU ID
                esAdmin = isAdmin,
                vmTicket = vmTicket,            // <<<<<<<<<< AQUI VA TU VIEWMODEL

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

                onEditar = {
                    onEditar(selectedProducto!!)
                    selectedProducto = null
                },

                onCerrar = { selectedProducto = null }
            )
        }
    }
}
