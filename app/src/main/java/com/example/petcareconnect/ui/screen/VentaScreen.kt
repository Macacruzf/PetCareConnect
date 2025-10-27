package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.petcareconnect.data.db.PetCareDatabase
import com.example.petcareconnect.data.repository.DetalleVentaRepository
import com.example.petcareconnect.data.repository.ProductoRepository
import com.example.petcareconnect.data.repository.VentaRepository
import com.example.petcareconnect.ui.viewmodel.VentaViewModel
import com.example.petcareconnect.ui.viewmodel.VentaViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VentaScreen() {
    val context = LocalContext.current

    // Instanciamos BD y repositorios
    val db = remember {
        Room.databaseBuilder(
            context,
            PetCareDatabase::class.java,
            "petcare_db"
        ).build()
    }

    val ventaRepo = remember { VentaRepository(db.ventaDao()) }
    val detalleRepo = remember { DetalleVentaRepository(db.detalleVentaDao()) }
    val vm: VentaViewModel = viewModel(factory = VentaViewModelFactory(ventaRepo, detalleRepo))
    val state by vm.state.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    // ---- UI principal ----
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Registro de Ventas",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))

            // 🔹 Detalles actuales (carrito)
            Text("Detalles de venta", fontWeight = FontWeight.SemiBold)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                itemsIndexed(state.detalles) { index, detalle ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = detalle.nombre, // ya accede al nombre
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("Cantidad: ${detalle.cantidad}")
                                Text("Subtotal: $${detalle.subtotal}")
                            }
                            IconButton(onClick = { vm.eliminarDetalle(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "Total: $${state.total}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            //  Campo cliente
            OutlinedTextField(
                value = state.cliente,
                onValueChange = vm::onClienteChange,
                label = { Text("Cliente") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // 🔹 Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ExtendedFloatingActionButton(
                    onClick = { showDialog = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Agregar producto") },
                    text = { Text("Agregar producto") },
                    containerColor = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = { vm.guardarVenta() }, // ahora sin parámetro
                    enabled = state.detalles.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Registrar venta")
                }
            }

            // 🔹 Mensajes de éxito o error
            state.successMsg?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = Color(0xFF2E7D32))
            }

            state.errorMsg?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }

        // 🔹 Diálogo para agregar producto
        if (showDialog) {
            DialogAgregarProductoVenta(
                onDismiss = { showDialog = false },
                onAgregar = { producto, precio, cantidad ->
                    vm.agregarDetalle(productoId = 1, nombre = producto, precio = precio, cantidad = cantidad)
                    showDialog = false
                }
            )
        }
    }
}

// ----------------------- DIÁLOGO PARA AGREGAR PRODUCTO -----------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogAgregarProductoVenta(
    onDismiss: () -> Unit,
    onAgregar: (String, Double, Int) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                val p = precio.toDoubleOrNull()
                val c = cantidad.toIntOrNull()
                if (p != null && c != null && nombre.isNotBlank()) {
                    onAgregar(nombre, p, c)
                }
            }) {
                Text("Agregar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancelar") }
        },
        title = { Text("Agregar producto a venta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre del producto") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = precio,
                    onValueChange = { precio = it },
                    label = { Text("Precio unitario") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { cantidad = it },
                    label = { Text("Cantidad") },
                    singleLine = true
                )
            }
        }
    )
}
