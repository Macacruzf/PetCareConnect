package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.petcareconnect.ui.viewmodel.PedidosViewModel

/*
 * Pantalla que muestra el historial de ventas registradas.
 * Su propósito es permitir la revisión de pedidos o compras ya realizadas,
 * mostrando detalles como fecha, método de pago y monto total.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialVentasScreen(
    pedidosViewModel: PedidosViewModel = viewModel(), // ViewModel que administra los datos de ventas
    onVolver: () -> Unit = {}                         // Acción al presionar el botón de retroceso
) {
    // Estado observable que contiene la lista de ventas registradas
    val historial by pedidosViewModel.historialVentas.collectAsState()

    // Estructura principal de la pantalla
    Scaffold(
        // Barra superior de navegación
        topBar = {
            TopAppBar(
                title = { Text("Historial de Ventas") },
                navigationIcon = {
                    // Botón de retroceso para volver a la pantalla anterior
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        // Contenedor principal con alineación superior
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // Si no hay ventas registradas, se muestra un mensaje informativo
            if (historial.isEmpty()) {
                Text(
                    text = "No hay ventas registradas aún.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                // Lista vertical que muestra cada venta registrada
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Por cada venta en el historial, se genera una tarjeta informativa
                    items(historial) { venta ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF8F8F8) // Fondo claro del card
                            ),
                            elevation = CardDefaults.cardElevation(6.dp) // Sombra con profundidad
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Título con el número de venta
                                Text(
                                    text = "Venta #${venta.idVenta}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF2196F3) // Azul representativo de PetCare Connect
                                )

                                Spacer(Modifier.height(6.dp))

                                // Información detallada de la venta
                                Text("Fecha: ${venta.fecha}")
                                Text("Método de pago: ${venta.metodoPago}")
                                Text("Total: $${String.format("%.2f", venta.total)}")
                            }
                        }
                    }
                }
            }
        }
    }
}
