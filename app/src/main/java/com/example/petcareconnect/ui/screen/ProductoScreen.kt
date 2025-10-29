package com.example.petcareconnect.ui.screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.petcareconnect.data.db.PetCareDatabase
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.repository.CategoriaRepository
import com.example.petcareconnect.data.repository.ProductoRepository
import com.example.petcareconnect.ui.viewmodel.ProductoViewModel
import com.example.petcareconnect.ui.viewmodel.ProductoViewModelFactory

// ---------------------------------------------------------------------------
// PANTALLA PRINCIPAL DE PRODUCTOS
// ---------------------------------------------------------------------------
// Esta pantalla muestra el catálogo de productos disponible en la aplicación.
// Su comportamiento cambia según el rol del usuario:
//  - ADMIN: puede crear y eliminar productos.
//  - CLIENTE o INVITADO: puede visualizar y agregar productos al carrito.
// Incluye filtros por categoría y animaciones suaves de Material 3.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoScreen(
    rol: String? = null,
    onAgregarAlCarrito: (Producto) -> Unit = {}
) {
    // Se obtiene el contexto de Android actual.
    val context = LocalContext.current

    // Se inicializa la base de datos local Room y los repositorios de producto y categoría.
    val db = remember {
        Room.databaseBuilder(
            context,
            PetCareDatabase::class.java,
            "petcare_db"
        ).build()
    }
    val productoRepo = remember { ProductoRepository(db.productoDao()) }
    val categoriaRepo = remember { CategoriaRepository(db.categoriaDao()) }

    // Se crea el ViewModel utilizando una fábrica personalizada.
    val vm: ProductoViewModel = viewModel(factory = ProductoViewModelFactory(productoRepo, categoriaRepo))
    val state by vm.state.collectAsState()

    // Se recargan las categorías al iniciarse la pantalla.
    LaunchedEffect(Unit) {
        vm.recargarCategoriasManualmente()
    }

    // Variables de estado local (controlan el diálogo, filtros y selección actual).
    var showDialog by remember { mutableStateOf(false) }
    var selectedProducto by remember { mutableStateOf<Producto?>(null) }
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }
    var expandedFiltro by remember { mutableStateOf(false) }

    // Identificación del rol del usuario.
    val isAdmin = rol == "ADMIN"
    val isCliente = rol == "CLIENTE" || rol == "INVITADO"

    // Filtrado dinámico de productos según la categoría seleccionada.
    val productosFiltrados = remember(state.productos, categoriaSeleccionada) {
        if (categoriaSeleccionada == "Todas") state.productos
        else state.productos.filter { producto ->
            val categoria = state.categorias.firstOrNull { it.idCategoria == producto.categoriaId }?.nombre
            categoria == categoriaSeleccionada
        }
    }

    // Contenedor principal de la pantalla.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Column {
            // Encabezado de la pantalla.
            Text(
                "Productos disponibles",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF2196F3)
            )
            Spacer(Modifier.height(8.dp))

            // Filtro de categorías (menú desplegable con animación integrada).
            val categorias = state.categorias

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
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )

                // Menú con efecto de expansión/cierre animado.
                ExposedDropdownMenu(
                    expanded = expandedFiltro,
                    onDismissRequest = { expandedFiltro = false }
                ) {
                    // Opción “Todas”.
                    DropdownMenuItem(
                        text = { Text("Todas") },
                        onClick = {
                            categoriaSeleccionada = "Todas"
                            expandedFiltro = false
                        }
                    )

                    // Si las categorías no están cargadas aún, se muestra un mensaje temporal.
                    if (categorias.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Cargando categorías...", color = Color.Gray) },
                            onClick = { expandedFiltro = false }
                        )
                    } else {
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
            }

            Spacer(Modifier.height(12.dp))

            // Lista desplazable de productos (LazyColumn usa carga progresiva y animación fluida).
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(productosFiltrados) { producto ->
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

            // Botón flotante de acción (solo visible para administradores).
            if (isAdmin) {
                ExtendedFloatingActionButton(
                    onClick = { showDialog = true },
                    icon = { Icon(Icons.Filled.Add, contentDescription = "Agregar producto") },
                    text = { Text("Nuevo producto") },
                    containerColor = Color(0xFF4CAF50)
                )
            }
        }

        // Diálogo modal para agregar un nuevo producto.
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

        // Diálogo de detalle del producto (se muestra al seleccionar uno en la lista).
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

// ---------------------------------------------------------------------------
// CARD DE PRODUCTO
// ---------------------------------------------------------------------------
// Componente que representa visualmente un producto individual.
// Muestra imagen, nombre, precio y stock.
// Cambia las acciones disponibles según el rol del usuario.
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
            .clickable { onClick() }, // Animación de clic con efecto ripple.
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto o ícono genérico si no existe.
            if (producto.imagenResId != null) {
                Image(
                    painter = painterResource(id = producto.imagenResId),
                    contentDescription = producto.nombre,
                    modifier = Modifier.size(70.dp).clip(MaterialTheme.shapes.small),
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

            // Acciones disponibles según el rol.
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

// ---------------------------------------------------------------------------
// DETALLE DE PRODUCTO CON RESEÑAS
// ---------------------------------------------------------------------------
// Diálogo emergente que muestra la información completa del producto.
// Permite al usuario agregarlo al carrito o escribir una reseña.
// Incluye animación de apertura y cierre integrada en AlertDialog.
@Composable
fun ProductoDetalleDialog(
    producto: Producto,
    esAdmin: Boolean,
    onAgregar: (Producto) -> Unit,
    onCerrar: () -> Unit
) {
    // Estados locales para manejar comentarios y calificaciones.
    val comentarios = remember { mutableStateListOf<String>() }
    var comentario by remember { mutableStateOf("") }
    var calificacion by remember { mutableStateOf(3) }

    AlertDialog(
        onDismissRequest = onCerrar,
        confirmButton = {
            // Si el usuario no es admin, muestra el botón para agregar al carrito.
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
                // Imagen principal del producto.
                producto.imagenResId?.let {
                    Image(
                        painter = painterResource(id = it),
                        contentDescription = producto.nombre,
                        modifier = Modifier.height(180.dp).fillMaxWidth().clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(Modifier.height(10.dp))
                Text("Precio: $${producto.precio}", style = MaterialTheme.typography.bodyLarge)
                Text("Stock disponible: ${producto.stock}", style = MaterialTheme.typography.bodyMedium)
                Divider(Modifier.padding(vertical = 10.dp))

                // Sección de reseñas.
                Text(" Opiniones de usuarios", fontWeight = FontWeight.Bold)
                if (comentarios.isEmpty()) {
                    Text("Aún no hay reseñas.", color = Color.Gray)
                } else {
                    comentarios.forEach {
                        Text(it, color = Color.DarkGray, modifier = Modifier.padding(4.dp))
                    }
                }

                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = comentario,
                    onValueChange = { comentario = it },
                    label = { Text("Escribe tu comentario") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Selección de calificación con estrellas.
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Calificación: ")
                    (1..5).forEach { i ->
                        IconButton(onClick = { calificacion = i }) {
                            Icon(
                                imageVector = if (i <= calificacion) Icons.Filled.Star else Icons.Filled.StarBorder,
                                contentDescription = null,
                                tint = Color(0xFFFFC107)
                            )
                        }
                    }
                }

                // Publicar una nueva reseña (actualiza la lista local).
                Button(
                    onClick = {
                        if (comentario.isNotBlank()) {
                            comentarios.add("⭐️$calificacion - $comentario")
                            comentario = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Publicar reseña")
                }
            }
        }
    )
}

// ---------------------------------------------------------------------------
// DIÁLOGO PARA AGREGAR PRODUCTO CON IMAGEN Y CATEGORÍA
// ---------------------------------------------------------------------------
// Muestra un formulario modal para registrar un nuevo producto.
// Permite seleccionar imagen desde recursos, asignar categoría y definir precio/stock.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogAgregarProducto(
    onDismiss: () -> Unit,
    onGuardar: () -> Unit,
    vm: ProductoViewModel
) {
    val state by vm.state.collectAsState()
    var expandedCategoria by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Variables locales para manejar la imagen seleccionada.
    var nombreDrawable by remember { mutableStateOf("") }
    var idDrawable by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                vm.insertProducto()
                onGuardar()
            }) { Text("Guardar") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancelar") }
        },
        title = { Text("Agregar producto") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                // Campo para cargar una imagen desde drawable.
                OutlinedTextField(
                    value = nombreDrawable,
                    onValueChange = { nombreDrawable = it },
                    label = { Text("Nombre de imagen (drawable)") },
                    placeholder = { Text("Ej: shampoo_perros") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Botón que carga la imagen de recursos del proyecto.
                Button(
                    onClick = {
                        val id = context.resources.getIdentifier(
                            nombreDrawable.trim(),
                            "drawable",
                            context.packageName
                        )
                        if (id != 0) {
                            idDrawable = id
                            vm.onImagenChange(id)
                        } else {
                            idDrawable = null
                            vm.onImagenChange(null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cargar imagen")
                }

                // Vista previa de la imagen cargada (con fondo gris si no existe).
                Box(
                    modifier = Modifier.size(120.dp).clip(MaterialTheme.shapes.medium).background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    if (idDrawable != null) {
                        Image(
                            painter = painterResource(id = idDrawable!!),
                            contentDescription = "Vista previa",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                // Campos del formulario (nombre, precio y stock).
                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = vm::onNombreChange,
                    label = { Text("Nombre del producto") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.precio,
                    onValueChange = vm::onPrecioChange,
                    label = { Text("Precio") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = state.stock,
                    onValueChange = vm::onStockChange,
                    label = { Text("Stock") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Menú desplegable para seleccionar categoría.
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
