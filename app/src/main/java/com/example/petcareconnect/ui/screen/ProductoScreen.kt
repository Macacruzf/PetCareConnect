package com.example.petcareconnect.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import coil.compose.rememberAsyncImagePainter
import com.example.petcareconnect.data.db.PetCareDatabase
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.model.EstadoProducto
import com.example.petcareconnect.data.repository.CategoriaRepository
import com.example.petcareconnect.data.repository.ProductoRepository
import com.example.petcareconnect.ui.viewmodel.ProductoViewModel
import com.example.petcareconnect.ui.viewmodel.ProductoViewModelFactory

// ---------------------------------------------------------
//                 PRODUCTO SCREEN
// ---------------------------------------------------------
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

    val vm: ProductoViewModel = viewModel(
        factory = ProductoViewModelFactory(productoRepo, categoriaRepo)
    )

    val state by vm.state.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var selectedProducto by remember { mutableStateOf<Producto?>(null) }
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }
    var expandedFiltro by remember { mutableStateOf(false) }

    val isAdmin = rol == "ADMIN"
    val isCliente = rol == "CLIENTE" || rol == "INVITADO"

    // FILTRO
    val productosFiltrados = remember(state.productos, categoriaSeleccionada) {
        if (categoriaSeleccionada == "Todas") state.productos
        else state.productos.filter {
            val cat = state.categorias.firstOrNull { c -> c.idCategoria == it.categoriaId }?.nombre
            cat == categoriaSeleccionada
        }
    }

    // -----------------------------------------------------
    // PANTALLA
    // -----------------------------------------------------
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
                color = Color(0xFF2196F3)
            )

            Spacer(Modifier.height(10.dp))

            // FILTRO POR CATEGORÍA
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

                    state.categorias.forEach { categoria ->
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

            Spacer(Modifier.height(12.dp))

            // LISTA DE PRODUCTOS
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(productosFiltrados) { producto ->
                    ProductoCard(
                        producto = producto,
                        isAdmin = isAdmin,
                        isCliente = isCliente,
                        onClick = { selectedProducto = producto },
                        onDelete = { vm.deleteProducto(producto.idProducto) },
                        onCambiarEstado = { estado ->
                            vm.cambiarEstadoManual(producto.idProducto, estado)
                        },
                        onAgregarAlCarrito = { onAgregarAlCarrito(producto) }
                    )
                }
            }

            if (isAdmin) {
                Spacer(Modifier.height(15.dp))
                ExtendedFloatingActionButton(
                    onClick = { showDialog = true },
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text("Nuevo producto") },
                    containerColor = Color(0xFF4CAF50)
                )
            }
        }

        // DIALOG AGREGAR
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

        // DIALOG DETALLE
        if (selectedProducto != null) {
            ProductoDetalleDialog(
                producto = selectedProducto!!,
                esAdmin = isAdmin,
                onCambiarEstado = { estado ->
                    vm.cambiarEstadoManual(selectedProducto!!.idProducto, estado)
                },
                onAgregar = {
                    onAgregarAlCarrito(it)
                    selectedProducto = null
                },
                onEliminar = {
                    vm.deleteProducto(selectedProducto!!.idProducto)
                    selectedProducto = null
                },
                onCerrar = { selectedProducto = null }
            )
        }
    }
}

// ---------------------------------------------------------
//                CARD PRODUCTO
// ---------------------------------------------------------
@Composable
fun ProductoCard(
    producto: Producto,
    isAdmin: Boolean,
    isCliente: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onCambiarEstado: (EstadoProducto) -> Unit,
    onAgregarAlCarrito: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Imagen (URI o drawable)
            when {
                // 1) Imagen desde URI (galería/cámara)
                producto.imagenUri != null -> {
                    Image(
                        painter = rememberAsyncImagePainter(model = producto.imagenUri),
                        contentDescription = producto.nombre,
                        modifier = Modifier
                            .size(65.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                // 2) Imagen por recurso drawable (semillas)
                producto.imagenResId != null -> {
                    Image(
                        painter = painterResource(id = producto.imagenResId),
                        contentDescription = producto.nombre,
                        modifier = Modifier
                            .size(65.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                // 3) Icono por defecto
                else -> {
                    Icon(
                        Icons.Default.Pets,
                        contentDescription = null,
                        modifier = Modifier.size(65.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold)
                Text("Precio: $${producto.precio}")
                Text("Stock: ${producto.stock}")

                val estadoColor = when (producto.estado) {
                    EstadoProducto.DISPONIBLE -> Color(0xFF4CAF50)
                    EstadoProducto.NO_DISPONIBLE -> Color(0xFF9E9E9E)
                    EstadoProducto.SIN_STOCK -> Color(0xFFFF9800)
                }

                Text(
                    "Estado: ${producto.estado.name.lowercase().replaceFirstChar { it.uppercase() }}",
                    color = estadoColor,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // ACCIONES ADMIN
            if (isAdmin) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    IconButton(onClick = { onCambiarEstado(EstadoProducto.DISPONIBLE) }) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = "Disponible", tint = Color(0xFF4CAF50))
                    }
                    IconButton(onClick = { onCambiarEstado(EstadoProducto.NO_DISPONIBLE) }) {
                        Icon(Icons.Filled.Block, contentDescription = "No disponible", tint = Color(0xFF616161))
                    }
                    IconButton(onClick = { onCambiarEstado(EstadoProducto.SIN_STOCK) }) {
                        Icon(Icons.Filled.Warning, contentDescription = "Sin stock", tint = Color(0xFFFF9800))
                    }

                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = Color(0xFFD32F2F))
                    }

                    // Pequeña ayuda visual
                    Text("Disp / No / Sin", style = MaterialTheme.typography.labelSmall)
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

// ---------------------------------------------------------
//             DIALOG DETALLE PRODUCTO
// ---------------------------------------------------------
@Composable
fun ProductoDetalleDialog(
    producto: Producto,
    esAdmin: Boolean,
    onCambiarEstado: (EstadoProducto) -> Unit,
    onAgregar: (Producto) -> Unit,
    onEliminar: () -> Unit,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(producto.nombre, fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // Imagen (URI o drawable)
                when {
                    producto.imagenUri != null -> {
                        Image(
                            painter = rememberAsyncImagePainter(model = producto.imagenUri),
                            contentDescription = null,
                            modifier = Modifier
                                .height(180.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    producto.imagenResId != null -> {
                        Image(
                            painter = painterResource(id = producto.imagenResId),
                            contentDescription = null,
                            modifier = Modifier
                                .height(180.dp)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                Text("Precio: $${producto.precio}")
                Text("Stock: ${producto.stock}")
                Text("Estado: ${producto.estado.name}")

                if (esAdmin) {
                    Spacer(Modifier.height(15.dp))
                    Divider()
                    Spacer(Modifier.height(8.dp))

                    Text("Cambiar estado:", fontWeight = FontWeight.Bold)

                    Row(horizontalArrangement = Arrangement.SpaceEvenly) {

                        IconButton(onClick = { onCambiarEstado(EstadoProducto.DISPONIBLE) }) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = "Disponible", tint = Color(0xFF4CAF50))
                        }

                        IconButton(onClick = { onCambiarEstado(EstadoProducto.NO_DISPONIBLE) }) {
                            Icon(Icons.Filled.Block, contentDescription = "No disponible", tint = Color(0xFF616161))
                        }

                        IconButton(onClick = { onCambiarEstado(EstadoProducto.SIN_STOCK) }) {
                            Icon(Icons.Filled.Warning, contentDescription = "Sin stock", tint = Color(0xFFFF9800))
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!esAdmin) {
                Button(onClick = { onAgregar(producto) }) {
                    Text("Agregar al carrito")
                }
            } else {
                OutlinedButton(onClick = onCerrar) { Text("Cerrar") }
            }
        },
        dismissButton =
            if (esAdmin) {
                {
                    OutlinedButton(onClick = onEliminar) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                        Text(" Eliminar")
                    }
                }
            } else null
    )
}

// ---------------------------------------------------------
//             DIALOG AGREGAR PRODUCTO
// ---------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogAgregarProducto(
    onDismiss: () -> Unit,
    onGuardar: () -> Unit,
    vm: ProductoViewModel
) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    var expandedCategoria by remember { mutableStateOf(false) }
    var expandedEstado by remember { mutableStateOf(false) }

    // Launcher único para cámara/galería (el sistema ofrece ambas opciones)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        vm.onImagenUriChange(uri?.toString())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onGuardar) {
                Text("Guardar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancelar") }
        },
        title = { Text("Agregar producto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                Text("Imagen del producto", fontWeight = FontWeight.SemiBold)

                // Botones "Cámara" y "Galería" (ambos abren el selector del sistema)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Cámara")
                    }
                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Galería")
                    }
                }

                // Vista previa
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.imagenUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(model = state.imagenUri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Pets, contentDescription = null, tint = Color.Gray)
                    }
                }

                // NOMBRE
                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = vm::onNombreChange,
                    label = { Text("Nombre del producto") },
                    modifier = Modifier.fillMaxWidth()
                )

                // PRECIO SOLO NÚMEROS (y punto)
                OutlinedTextField(
                    value = state.precio,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() || c == '.' }) vm.onPrecioChange(it)
                    },
                    label = { Text("Precio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // STOCK SOLO NÚMEROS
                OutlinedTextField(
                    value = state.stock,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() }) vm.onStockChange(it)
                    },
                    label = { Text("Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // CATEGORÍA
                ExposedDropdownMenuBox(
                    expanded = expandedCategoria,
                    onExpandedChange = { expandedCategoria = !expandedCategoria }
                ) {

                    OutlinedTextField(
                        value = state.categorias.firstOrNull { it.idCategoria == state.categoriaId }?.nombre
                            ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
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

                // ESTADO
                ExposedDropdownMenuBox(
                    expanded = expandedEstado,
                    onExpandedChange = { expandedEstado = !expandedEstado }
                ) {

                    OutlinedTextField(
                        value = state.estado.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEstado)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedEstado,
                        onDismissRequest = { expandedEstado = false }
                    ) {

                        EstadoProducto.values().forEach { estado ->
                            DropdownMenuItem(
                                text = { Text(estado.name) },
                                onClick = {
                                    vm.onEstadoChange(estado)
                                    expandedEstado = false
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}
