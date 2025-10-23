package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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
import com.example.petcareconnect.data.model.DetalleVenta
import com.example.petcareconnect.data.model.Venta
import com.example.petcareconnect.data.repository.DetalleVentaRepository
import com.example.petcareconnect.data.repository.VentaRepository
import com.example.petcareconnect.ui.viewmodel.VentaViewModel
import com.example.petcareconnect.ui.viewmodel.VentaViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialVentasScreen() {
    val context = LocalContext.current

    // --- Instanciamos BD y repos ---
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

    var ventaSeleccionada by remember { mutableStateOf<Venta?>(null) }
    var detallesVenta by remember { mutableStateOf<List<DetalleVenta>>(emptyList()) }

    val scope = rememberCoroutineScope()

    // --- Pantalla principal ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Historial de Ventas",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(state.ventas) { venta ->
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Fecha: ${venta.fecha}", fontWeight = FontWeight.SemiBold)
                                Text("Cliente: ${venta.cliente}")
                                Text("Total: $${venta.total}")
                            }

                            IconButton(onClick = {
                                ventaSeleccionada = venta
                                scope.launch {
                                    detallesVenta = detalleRepo.getByVentaId(venta.idVenta)
                                }
                            }) {
                                Icon(Icons.Default.Info, contentDescription = "Ver detalle")
                            }
                        }
                    }
                }
            }
        }

        // --- Diálogo con detalles de venta seleccionada ---
        if (ventaSeleccionada != null) {
            AlertDialog(
                onDismissRequest = { ventaSeleccionada = null },
                confirmButton = {
                    TextButton(onClick = { ventaSeleccionada = null }) {
                        Text("Cerrar")
                    }
                },
                title = { Text("Detalles de venta") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Cliente: ${ventaSeleccionada?.cliente}", fontWeight = FontWeight.SemiBold)
                        Text("Fecha: ${ventaSeleccionada?.fecha}")
                        Text("Total: $${ventaSeleccionada?.total}")
                        Divider()
                        Text("Productos:", fontWeight = FontWeight.Bold)

                        if (detallesVenta.isEmpty()) {
                            Text("No hay detalles registrados para esta venta.")
                        } else {
                            detallesVenta.forEach { d ->
                                Text("- ${d.nombre} (x${d.cantidad}) — $${d.subtotal}")
                            }
                        }
                    }
                }
            )
        }
    }
}

