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
import com.example.petcareconnect.ui.viewmodel.Pedido
import com.example.petcareconnect.ui.viewmodel.PedidosViewModel

/*
 * Pantalla que muestra la lista de pedidos registrados por el cliente o el administrador.
 * Permite visualizar detalles como fecha, total, método de pago y productos.
 * Si el usuario es administrador, se habilita la opción para marcar pedidos como entregados.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosClienteScreen(
    pedidosViewModel: PedidosViewModel, // ViewModel encargado de la gestión de pedidos
    rol: String?,                       // Rol del usuario actual (ADMIN o CLIENTE)
    onVolver: () -> Unit                // Acción al presionar el botón de retroceso
) {
    // Observa el flujo de pedidos en tiempo real desde el ViewModel
    val pedidos by pedidosViewModel.pedidos.collectAsState()

    // Estructura principal con barra superior y contenido desplazable
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pedidos Registrados") },
                navigationIcon = {
                    // Botón de regreso a la pantalla anterior
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        // Contenedor principal con márgenes internos
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // Si no existen pedidos registrados, se muestra un mensaje informativo
            if (pedidos.isEmpty()) {
                Text(
                    text = "No hay pedidos registrados aún.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                // Lista desplazable con separación entre los elementos
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Renderiza cada pedido dentro de una tarjeta visual (Card)
                    items(pedidos) { pedido ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF8F8F8)
                            ),
                            elevation = CardDefaults.cardElevation(6.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Encabezado con el número de pedido
                                Text(
                                    text = "Pedido #${pedido.id}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color(0xFF2196F3)
                                )

                                Spacer(Modifier.height(6.dp))

                                // Información general del pedido
                                Text("Fecha: ${pedido.fecha}")
                                Text("Método de pago: ${pedido.metodoPago}")
                                Text("Total: $${String.format("%.2f", pedido.total)}")

                                Spacer(Modifier.height(8.dp))

                                // Estado actual del pedido (entregado o pendiente)
                                Text(
                                    "Estado: ${pedido.estado}",
                                    color = if (pedido.estado == "Entregado")
                                        Color(0xFF4CAF50) // Verde si fue entregado
                                    else
                                        Color(0xFFFF9800), // Naranja si está pendiente
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(Modifier.height(10.dp))
                                Divider()

                                // Sección con los productos incluidos en el pedido
                                Text(
                                    "Productos:",
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF4CAF50),
                                    modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
                                )

                                // Listado interno de productos del pedido
                                pedido.items.forEach { item ->
                                    Text("- ${item.nombre} (${item.cantidad} x $${item.precio})")
                                }

                                // Solo los administradores pueden cambiar el estado a "Entregado"
                                if (rol == "ADMIN" && pedido.estado != "Entregado") {
                                    Spacer(Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            pedidosViewModel.marcarComoEntregado(pedido.id)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF4CAF50)
                                        ),
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text("Marcar como entregado", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
