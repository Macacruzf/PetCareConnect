package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import coil.compose.rememberAsyncImagePainter
import com.example.petcareconnect.data.db.PetCareDatabase
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.repository.CategoriaRepository
import com.example.petcareconnect.data.repository.ProductoRepository
import com.example.petcareconnect.ui.viewmodel.ProductoViewModel
import com.example.petcareconnect.ui.viewmodel.ProductoViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoScreen(
    rol: String? = null,
    onAgregarAlCarrito: (Producto) -> Unit = {}
) {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(
            context,
            PetCareDatabase::class.java,
            "petcare_db"
        ).build()
    }

    val productoRepo = remember { ProductoRepository(db.productoDao()) }
    val categoriaRepo = remember { CategoriaRepository(db.categoriaDao()) }
    val vm: ProductoViewModel = viewModel(factory = ProductoViewModelFactory(productoRepo, categoriaRepo))
    val state by vm.state.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var selectedProducto by remember { mutableStateOf<Producto?>(null) }

    val isAdmin = rol == "ADMIN"
    val isCliente = rol == "CLIENTE" || rol == "INVITADO"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Column {
            Text(
                "Productos disponibles",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))

            // 🔹 Lista de productos
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(state.productos) { producto ->
                    ProductoCard(
                        producto = producto,
                        isAdmin = isAdmin,
                        isCliente = isCliente,
                        onClick = { selectedProducto = producto },
                        onDelete = { vm.deleteProducto(producto.idProducto) },
                        onAgregarAlCarrito = { onAgregarAlCarrito(producto) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // 🔹 Solo admin puede agregar
            if (isAdmin) {
                ExtendedFloatingActionButton(
                    onClick = { showDialog = true },
                    icon = { Icon(Icons.Filled.Add, contentDescription = "Agregar producto") },
                    text = { Text("Nuevo producto") },
                    containerColor = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 🔹 Diálogo de nuevo producto
        if (showDialog) {
            DialogAgregarProducto(
                onDismiss = { showDialog = false },
                onGuardar = {
                    vm.insertProducto()
                    showDialog = false
                },
                vm = vm
            )
        }

        // 🔹 Detalle del producto
        if (selectedProducto != null) {
            ProductoDetalleDialog(
                producto = selectedProducto!!,
                esAdmin = isAdmin,
                onAgregar = {
                    onAgregarAlCarrito(it)
                    selectedProducto = null
                },
                onCerrar = { selectedProducto = null }
            )
        }
    }
}

// ----------------------- CARD DE PRODUCTO -----------------------
@Composable
fun ProductoCard(
    producto: Producto,
    isAdmin: Boolean,
    isCliente: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onAgregarAlCarrito: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (producto.imagenResId != null) {
                Image(
                    painter = painterResource(id = producto.imagenResId),
                    contentDescription = producto.nombre,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Pets,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(70.dp)
                )
            }

            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Precio: $${producto.precio}", style = MaterialTheme.typography.bodySmall)
                Text("Stock: ${producto.stock}", style = MaterialTheme.typography.bodySmall)
            }

            if (isAdmin) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = Color.Red)
                }
            } else if (isCliente) {
                Button(
                    onClick = onAgregarAlCarrito,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Agregar")
                }
            }
        }
    }
}

// ----------------------- DETALLE DE PRODUCTO -----------------------
@Composable
fun ProductoDetalleDialog(
    producto: Producto,
    esAdmin: Boolean,
    onAgregar: (Producto) -> Unit,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        confirmButton = {
            if (!esAdmin) {
                Button(onClick = { onAgregar(producto) }) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Agregar al carrito")
                }
            } else {
                OutlinedButton(onClick = onCerrar) { Text("Cerrar") }
            }
        },
        title = { Text(producto.nombre, fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (producto.imagenResId != null) {
                    Image(
                        painter = painterResource(id = producto.imagenResId),
                        contentDescription = producto.nombre,
                        modifier = Modifier
                            .height(180.dp)
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text("Precio: $${producto.precio}", style = MaterialTheme.typography.bodyLarge)
                Text("Stock disponible: ${producto.stock}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    )
}

// ----------------------- DIÁLOGO PARA AGREGAR PRODUCTO -----------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogAgregarProducto(
    onDismiss: () -> Unit,
    onGuardar: () -> Unit,
    vm: ProductoViewModel
) {
    val state by vm.state.collectAsState()
    var expandedCategoria by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onGuardar) { Text("Guardar") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancelar") }
        },
        title = { Text("Agregar producto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = vm::onNombreChange,
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.precio,
                    onValueChange = vm::onPrecioChange,
                    label = { Text("Precio") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.stock,
                    onValueChange = vm::onStockChange,
                    label = { Text("Stock") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expandedCategoria,
                    onExpandedChange = { expandedCategoria = !expandedCategoria }
                ) {
                    OutlinedTextField(
                        value = state.categorias.firstOrNull { it.idCategoria == state.categoriaId }?.nombre ?: "",
                        onValueChange = {},
                        label = { Text("Categoría") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) }
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCategoria,
                        onDismissRequest = { expandedCategoria = false }
                    ) {
                        state.categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria.nombre) },
                                onClick = {
                                    vm.onCategoriaChange(categoria.idCategoria)
                                    expandedCategoria = false
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}
