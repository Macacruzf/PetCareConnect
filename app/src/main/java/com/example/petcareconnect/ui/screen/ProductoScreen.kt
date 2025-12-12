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
import coil.compose.rememberAsyncImagePainter
import com.example.petcareconnect.data.model.EstadoProducto
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.ui.theme.PetBlueAccent
import com.example.petcareconnect.ui.theme.PetGreenPrimary
import com.example.petcareconnect.ui.theme.PetOrangeSecondary
import com.example.petcareconnect.ui.viewmodel.ProductoViewModel
import java.io.File

// =====================================================================
// ========================= PRODUCTO SCREEN ============================
// =====================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoScreen(
    rol: String? = null,
    productoViewModel: ProductoViewModel,
    onAgregarAlCarrito: (Producto) -> Unit = {},
    onEditarProducto: (Producto) -> Unit = {}
) {

    val vm = productoViewModel
    val state by vm.state.collectAsState()

    var showDialogAgregar by remember { mutableStateOf(false) }
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }
    var expandedFiltro by remember { mutableStateOf(false) }

    // ⭐ NUEVO → Guardar el producto clickeado
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }

    val isAdmin = rol == "ADMIN"
    val isCliente = rol == "CLIENTE" || rol == "INVITADO"

    val snackbarHostState = remember { SnackbarHostState() }

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

    val productosFiltrados = remember(state.productos, categoriaSeleccionada) {
        if (categoriaSeleccionada == "Todas") state.productos
        else state.productos.filter {
            val catNombre =
                state.categorias.firstOrNull { c -> c.idCategoria == it.categoriaId }?.nombre
            catNombre == categoriaSeleccionada
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (isAdmin)
                ExtendedFloatingActionButton(
                    onClick = { showDialogAgregar = true },
                    icon = { Icon(Icons.Filled.Add, "Nuevo producto") },
                    text = { Text("Nuevo producto") },
                    containerColor = PetGreenPrimary,
                    contentColor = Color.White
                )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Text(
                "Productos disponibles",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = PetBlueAccent
            )

            Spacer(Modifier.height(10.dp))

            // ================= FILTRO =================
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

            Spacer(Modifier.height(10.dp))

            // ================= LISTA =================
            if (productosFiltrados.isEmpty()) {
                Text("No hay productos registrados.")
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(productosFiltrados) { producto ->

                        ProductoCard(
                            producto = producto,
                            isAdmin = isAdmin,
                            isCliente = isCliente,

                            // ⭐ CUANDO SE HACE CLICK → abrir diálogo
                            onClick = {
                                productoSeleccionado = producto
                            },

                            onEditar = {
                                onEditarProducto(producto)
                            },

                            onDelete = {
                                vm.deleteProducto(producto.idProducto)
                            },

                            onAgregarAlCarrito = {
                                onAgregarAlCarrito(producto)
                            },

                            onCambiarEstado = { estado ->
                                vm.cambiarEstadoManual(producto.idProducto, estado)
                            }
                        )
                    }
                }
            }
        }

        // ================= DIALOGO AGREGAR =================
        if (showDialogAgregar) {
            DialogAgregarProducto(
                vm = vm,
                onDismiss = { showDialogAgregar = false },
                onGuardar = {
                    vm.insertProducto()
                    showDialogAgregar = false
                }
            )
        }

        // ================= ⭐ DIALOGO DETALLE PRODUCTO =================
        productoSeleccionado?.let { prod ->
            DialogDetalleProducto(
                producto = prod,
                onClose = { productoSeleccionado = null }
            )
        }
    }
}

// =====================================================================
// ======================= PRODUCTO CARD ================================
// =====================================================================
@Composable
fun ProductoCard(
    producto: Producto,
    isAdmin: Boolean,
    isCliente: Boolean,
    onClick: () -> Unit,
    onEditar: () -> Unit,
    onDelete: () -> Unit,
    onCambiarEstado: (EstadoProducto) -> Unit,
    onAgregarAlCarrito: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clickable { onClick() }, // ⭐ ABRE DETALLE
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(Modifier.padding(12.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                // ✅ PRIORIDAD: imagenUrl (backend) > imagenUri (local) > imagenResId (drawable) > icono default
                when {
                    producto.imagenUrl != null -> {
                        Image(
                            painter = rememberAsyncImagePainter(producto.imagenUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    producto.imagenUri != null -> {
                        Image(
                            painter = rememberAsyncImagePainter(producto.imagenUri),
                            contentDescription = null,
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    producto.imagenResId != null -> {
                        Image(
                            painter = painterResource(producto.imagenResId),
                            contentDescription = null,
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    else -> {
                        Icon(
                            Icons.Default.Pets,
                            contentDescription = null,
                            modifier = Modifier.size(70.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(producto.nombre, fontWeight = FontWeight.Bold)
                    Text("Precio: $${producto.precio}")
                    Text("Stock: ${producto.stock}")

                    Text(
                        when (producto.estado) {
                            EstadoProducto.DISPONIBLE -> "Estado: Disponible"
                            EstadoProducto.NO_DISPONIBLE -> "Estado: No disponible"
                            EstadoProducto.SIN_STOCK -> "Estado: Sin stock"
                        },
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            if (isAdmin) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { onCambiarEstado(EstadoProducto.DISPONIBLE) }) {
                        Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF4CAF50))
                    }
                    IconButton(onClick = { onCambiarEstado(EstadoProducto.NO_DISPONIBLE) }) {
                        Icon(Icons.Filled.Block, null, tint = Color(0xFF616161))
                    }
                    IconButton(onClick = { onCambiarEstado(EstadoProducto.SIN_STOCK) }) {
                        Icon(Icons.Filled.Warning, null, tint = Color(0xFFFF9800))
                    }
                    IconButton(onClick = onEditar) {
                        Icon(Icons.Filled.Edit, null, tint = Color(0xFF2196F3))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, null, tint = Color(0xFFD32F2F))
                    }
                }

            } else if (isCliente) {
                val puedeAgregar =
                    producto.stock > 0 && producto.estado == EstadoProducto.DISPONIBLE

                Button(
                    onClick = onAgregarAlCarrito,
                    enabled = puedeAgregar,
                    colors = ButtonDefaults.buttonColors(containerColor = PetOrangeSecondary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.ShoppingCart, null)
                    Spacer(Modifier.width(6.dp))
                    Text(if (puedeAgregar) "Agregar" else "Sin stock")
                }
            }
        }
    }
}

// =====================================================================
// ===================== DIALOG DETALLE PRODUCTO ========================
// =====================================================================
@Composable
fun DialogDetalleProducto(
    producto: Producto,
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(producto.nombre, fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // ✅ PRIORIDAD: imagenUrl (backend) > imagenUri (local) > imagenResId (drawable) > icono default
                when {
                    producto.imagenUrl != null -> {
                        Image(
                            painter = rememberAsyncImagePainter(producto.imagenUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    producto.imagenUri != null -> {
                        Image(
                            painter = rememberAsyncImagePainter(producto.imagenUri),
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    producto.imagenResId != null -> {
                        Image(
                            painter = painterResource(producto.imagenResId),
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    else -> {
                        Icon(
                            Icons.Default.Pets,
                            contentDescription = null,
                            modifier = Modifier.size(200.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))
                Text("Precio: $${producto.precio}")
                Text("Stock: ${producto.stock}")
                Text(
                    "Estado: " + when (producto.estado) {
                        EstadoProducto.DISPONIBLE -> "Disponible"
                        EstadoProducto.NO_DISPONIBLE -> "No disponible"
                        EstadoProducto.SIN_STOCK -> "Sin stock"
                    }
                )
            }
        },
        confirmButton = {
            Button(onClick = onClose) {
                Text("Cerrar")
            }
        }
    )
}

// =====================================================================
// ===================== DIALOG AGREGAR PRODUCTO ========================
// =====================================================================
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
    var photoUriState by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUriState != null)
            vm.onImagenUriChange(photoUriState.toString())
    }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                val file =
                    File(context.cacheDir, "foto_${System.currentTimeMillis()}.jpg")
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                photoUriState = uri
                cameraLauncher.launch(uri)
            } else Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }

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

                Text("Imagen", fontWeight = FontWeight.Bold)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PhotoCamera, null)
                        Spacer(Modifier.width(4.dp))
                        Text("Cámara")
                    }
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Image, null)
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
                            painter = rememberAsyncImagePainter(state.imagenUri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Pets, null, tint = Color.Gray)
                    }
                }

                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = vm::onNombreChange,
                    label = { Text("Nombre") },
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
            }
        }
    )
}
