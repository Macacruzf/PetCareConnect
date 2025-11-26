package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.petcareconnect.data.model.Carrito

@Composable
fun DetalleVentaScreen(
    total: Double,
    items: List<Carrito>,
    metodoPago: String = "Tarjeta",
    onFinalizar: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {

            // --------------------------------------
            // TÍTULO
            // --------------------------------------
            Text(
                text = "Detalle de la Venta",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF2E7D32)
            )

            // --------------------------------------
            // LISTA DE PRODUCTOS
            // --------------------------------------
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                if (items.isEmpty()) {
                    item {
                        Text(
                            text = "No hay productos en esta venta",
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 40.dp)
                        )
                    }
                } else {
                    items(items) { item ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            Column(Modifier.padding(14.dp)) {

                                Text(
                                    text = item.nombre,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF1B5E20)
                                )

                                Spacer(Modifier.height(6.dp))

                                Text(
                                    text = "Cantidad: ${item.cantidad}",
                                    color = Color.DarkGray
                                )

                                Text(
                                    text = "Subtotal: $${String.format("%.2f", item.precio * item.cantidad)}",
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // DIVISOR
            Divider(thickness = 1.dp, color = Color.LightGray)

            // --------------------------------------
            // TOTAL Y MÉTODO DE PAGO
            // --------------------------------------
            Column(horizontalAlignment = Alignment.Start) {

                Text(
                    text = "Total Pagado:",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )

                Text(
                    text = "$${String.format("%.2f", total)}",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF2E7D32)
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Método de Pago: $metodoPago",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        }

        // --------------------------------------
        // BOTÓN FINALIZAR (FIJO ABAJO)
        // --------------------------------------
        Button(
            onClick = onFinalizar,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
        ) {
            Text(
                "Finalizar y volver al inicio",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
