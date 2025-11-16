package com.example.petcareconnect.ui.screen

import android.Manifest
import android.net.Uri
import android.widget.Toast
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
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import coil.compose.rememberAsyncImagePainter
import com.example.petcareconnect.data.db.PetCareDatabase
import com.example.petcareconnect.data.model.EstadoProducto
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.repository.CategoriaRepository
import com.example.petcareconnect.data.repository.ProductoRepository
import com.example.petcareconnect.ui.theme.PetBlueAccent
import com.example.petcareconnect.ui.theme.PetGreenPrimary
import com.example.petcareconnect.ui.theme.PetOrangeSecondary
import com.example.petcareconnect.ui.viewmodel.ProductoViewModel
import com.example.petcareconnect.ui.viewmodel.ProductoViewModelFactory
import java.io.File

// ---------------------------------------------------------
//                 PRODUCTO SCREEN (con Scaffold)
// ---------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoScreen(
    rol: String? = null,
    onAgregarAlCarrito: (Producto) -> Unit = {}
) {
    val context = LocalContext.current

    // DB + repos
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

    var showDialogAgregar by remember { mutableStateOf(false) }
    var showDialogEditar by remember { mutableStateOf(false) }
    var selectedProducto by remember { mutableStateOf<Producto?>(null) }

    var categoriaSeleccionada by remember { mutableStateOf("Todas") }
    var expandedFiltro by remember { mutableStateOf(false) }

    val isAdmin = rol == "ADMIN"
    val isCliente = rol == "CLIENTE" || rol == "INVITADO"

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    // Mostrar mensajes de éxito / error
    LaunchedEffect(state.successMsg, state.errorMsg) {
        state.successMsg?.let {
            snackbarHostState.showSnackbar(it)
            vm.limpiarMensajes()
        }
        state.errorMsg?.let {
            snackbarHostState.showSnackbar(it)
            vm.limpiarMensajes()
        }
    }

    // FILTRO POR CATEGORÍA
    val productosFiltrados = remember(state.productos, categoriaSeleccionada) {
        if (categoriaSeleccionada == "Todas") state.productos
        else state.productos.filter {
            val cat = state.categorias.firstOrNull { c -> c.idCategoria == it.categoriaId }?.nombre
            cat == categoriaSeleccionada
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (isAdmin) {
                ExtendedFloatingActionButton(
                    onClick = { showDialogAgregar = true },
                    icon = { Icon(Icons.Filled.Add, contentDescription = "Nuevo producto") },
                    text = { Text("Nuevo producto") },
                    containerColor = PetGreenPrimary,
                    contentColor = Color.White
                )
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
                .padding(innerPadding)
        ) {
            Column {

                Text(
                    "Productos disponibles",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = PetBlueAccent
                )

                Spacer(Modifier.height(10.dp))

                // FILTRO CATEGORÍA
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

                if (productosFiltrados.isEmpty()) {
                    Text("No hay productos registrados aún.")
                } else {
                    // LISTA DE PRODUCTOS
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(productosFiltrados) { producto ->
                            ProductoCard(
                                producto = producto,
                                isAdmin = isAdmin,
                                isCliente = isCliente,
                                onClick = { selectedProducto = producto },
                                onDelete = { vm.deleteProducto(producto.idProducto) },
                                onEditar = {
                                    vm.cargarProductoParaEdicion(producto)
                                    showDialogEditar = true
                                },
                                onCambiarEstado = { estado ->
                                    vm.cambiarEstadoManual(producto.idProducto, estado)
                                },
                                onAgregarAlCarrito = { onAgregarAlCarrito(producto) }
                            )
                        }
                    }
                }
            }

            // ➤ AGREGAR PRODUCTO
            if (showDialogAgregar) {
                DialogAgregarProducto(
                    vm = vm,
                    onDismiss = {
                        vm.limpiarMensajes()
                        showDialogAgregar = false
                    },
                    onGuardar = {
                        vm.insertProducto()  // limpia formulario adentro
                        showDialogAgregar = false
                    }
                )
            }

            // ➤ EDITAR PRODUCTO
            if (showDialogEditar) {
                DialogEditarProducto(
                    vm = vm,
                    onDismiss = {
                        vm.limpiarMensajes()
                        showDialogEditar = false
                    },
                    onGuardar = {
                        vm.editarProducto()  // limpia formulario adentro
                        showDialogEditar = false
                    }
                )
            }

            // ➤ DETALLE PRODUCTO
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
                    onCerrar = { selectedProducto = null },
                    onEditar = {
                        vm.cargarProductoParaEdicion(selectedProducto!!)
                        selectedProducto = null
                        showDialogEditar = true
                    }
                )
            }
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
    onEditar: () -> Unit,
    onCambiarEstado: (EstadoProducto) -> Unit,
    onAgregarAlCarrito: () -> Unit
) {
    val puedeAgregar = producto.stock > 0 && producto.estado == EstadoProducto.DISPONIBLE

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Imagen
                when {
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
            }

            Spacer(Modifier.height(10.dp))

            // ⭐ ACCIONES EN HORIZONTAL ⭐
            if (isAdmin) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(onClick = { onCambiarEstado(EstadoProducto.DISPONIBLE) }) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50))
                    }

                    IconButton(onClick = { onCambiarEstado(EstadoProducto.NO_DISPONIBLE) }) {
                        Icon(Icons.Filled.Block, contentDescription = null, tint = Color(0xFF616161))
                    }

                    IconButton(onClick = { onCambiarEstado(EstadoProducto.SIN_STOCK) }) {
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFFF9800))
                    }

                    IconButton(onClick = onEditar) {
                        Icon(Icons.Filled.Edit, contentDescription = null, tint = Color(0xFF1976D2))
                    }

                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = null, tint = Color(0xFFD32F2F))
                    }
                }
            } else if (isCliente) {

                Button(
                    onClick = onAgregarAlCarrito,
                    enabled = puedeAgregar,
                    colors = ButtonDefaults.buttonColors(containerColor = PetOrangeSecondary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(if (puedeAgregar) "Agregar" else "Sin stock / no disponible")
                }
            }
        }
    }
}

// ---------------------------------------------------------
//        DIALOG DETALLE PRODUCTO
// ---------------------------------------------------------
@Composable
fun ProductoDetalleDialog(
    producto: Producto,
    esAdmin: Boolean,
    onCambiarEstado: (EstadoProducto) -> Unit,
    onAgregar: (Producto) -> Unit,
    onEliminar: () -> Unit,
    onCerrar: () -> Unit,
    onEditar: () -> Unit
) {
    val puedeAgregar = producto.stock > 0 && producto.estado == EstadoProducto.DISPONIBLE

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(producto.nombre, fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

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
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50)
                            )
                        }
                        IconButton(onClick = { onCambiarEstado(EstadoProducto.NO_DISPONIBLE) }) {
                            Icon(
                                Icons.Filled.Block,
                                contentDescription = null,
                                tint = Color(0xFF616161)
                            )
                        }
                        IconButton(onClick = { onCambiarEstado(EstadoProducto.SIN_STOCK) }) {
                            Icon(
                                Icons.Filled.Warning,
                                contentDescription = null,
                                tint = Color(0xFFFF9800)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!esAdmin) {
                Button(
                    onClick = { onAgregar(producto) },
                    enabled = puedeAgregar
                ) {
                    Text(if (puedeAgregar) "Agregar al carrito" else "Sin stock / no disponible")
                }
            } else {
                OutlinedButton(onClick = onCerrar) {
                    Text("Cerrar")
                }
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
//       DIALOG AGREGAR PRODUCTO
// ---------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogAgregarProducto(
    vm: ProductoViewModel,
    onDismiss: () -> Unit,
    onGuardar: () -> Unit
) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    var expandedCategoria by remember { mutableStateOf(false) }
    var expandedEstado by remember { mutableStateOf(false) }

    // CÁMARA
    val photoUriState = remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && photoUriState.value != null) {
                vm.onImagenUriChange(photoUriState.value.toString())
            }
        }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                val file = File(context.cacheDir, "producto_${System.currentTimeMillis()}.jpg")
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                photoUriState.value = uri
                cameraLauncher.launch(uri)
            } else {
                Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }

    // GALERÍA
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            vm.onImagenUriChange(uri?.toString())
        }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { Button(onClick = onGuardar) { Text("Guardar") } },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text("Agregar producto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                Text("Imagen del producto", fontWeight = FontWeight.Bold)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                    OutlinedButton(
                        onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Cámara")
                    }

                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Galería")
                    }
                }

                // VISTA PREVIA
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

                // FORM
                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = vm::onNombreChange,
                    label = { Text("Nombre del producto") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.precio,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() || c == '.' }) vm.onPrecioChange(it)
                    },
                    label = { Text("Precio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

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

// ---------------------------------------------------------
//       DIALOG EDITAR PRODUCTO
// ---------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogEditarProducto(
    vm: ProductoViewModel,
    onDismiss: () -> Unit,
    onGuardar: () -> Unit
) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current

    var expandedCategoria by remember { mutableStateOf(false) }
    var expandedEstado by remember { mutableStateOf(false) }

    // Cámara
    val photoUriState = remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && photoUriState.value != null) {
                vm.onImagenUriChange(photoUriState.value.toString())
            }
        }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                val file = File(context.cacheDir, "edit_${System.currentTimeMillis()}.jpg")
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                photoUriState.value = uri
                cameraLauncher.launch(uri)
            } else {
                Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }

    // Galería
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            vm.onImagenUriChange(uri?.toString())
        }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { Button(onClick = onGuardar) { Text("Actualizar") } },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancelar") } },
        title = { Text("Editar producto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                Text("Imagen del producto", fontWeight = FontWeight.Bold)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                    OutlinedButton(
                        onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Cámara")
                    }

                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Galería")
                    }
                }

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

                // FORM
                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = vm::onNombreChange,
                    label = { Text("Nombre del producto") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = state.precio,
                    onValueChange = {
                        if (it.all { c -> c.isDigit() || c == '.' }) vm.onPrecioChange(it)
                    },
                    label = { Text("Precio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

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
