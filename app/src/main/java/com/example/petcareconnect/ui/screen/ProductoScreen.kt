package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // ✅ Import agregado
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.petcareconnect.data.db.PetCareDatabase
import com.example.petcareconnect.data.repository.ProductoRepository
import com.example.petcareconnect.data.repository.CategoriaRepository
import com.example.petcareconnect.ui.viewmodel.ProductoViewModel
import com.example.petcareconnect.ui.viewmodel.ProductoViewModelFactory
import com.example.petcareconnect.data.model.Producto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductoScreen() {
    val context = LocalContext.current // ✅ ahora funciona
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

            // 🔹 Listado de productos
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(state.productos) { producto ->
                    ProductoCard(
                        producto = producto,
                        onDelete = { vm.deleteProducto(producto.idProducto) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // 🔹 Solo visible para admin (podrás controlarlo después con el rol del usuario)
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                icon = { Icon(Icons.Filled.Add, contentDescription = "Agregar producto") },
                text = { Text("Nuevo producto") },
                containerColor = MaterialTheme.colorScheme.primary
            )
        }

        // 🔹 Diálogo para agregar producto
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
    }
}

// ----------------------- CARD DE PRODUCTO -----------------------

@Composable
fun ProductoCard(producto: Producto, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.ShoppingBag,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Precio: $${producto.precio}", style = MaterialTheme.typography.bodySmall)
                Text("Stock: ${producto.stock}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = Color.Red)
            }
        }
    }
}

// ----------------------- DIÁLOGO DE NUEVO PRODUCTO -----------------------

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
            Button(onClick = onGuardar) {
                Text("Guardar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria)
                        }
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
