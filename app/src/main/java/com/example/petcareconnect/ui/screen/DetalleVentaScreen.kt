package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.petcareconnect.data.model.Carrito

/*
 * Pantalla que muestra el detalle final de una venta o pedido.
 * Resume los productos adquiridos, el método de pago y el total abonado.
 * Permite finalizar la compra y regresar al inicio.
 */
@Composable
fun DetalleVentaScreen(
    total: Double,                      // Monto total pagado
    items: List<Carrito>,               // Lista de productos comprados
    metodoPago: String = "No especificado", // Método de pago utilizado
    onFinalizar: () -> Unit             // Acción para volver a la pantalla principal
) {
    // Contenedor principal que ocupa toda la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // Estructura vertical con espaciado entre elementos
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Encabezado del detalle de venta
            Text(
                "Detalle de la Venta",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF4CAF50)
            )

            // Lista con desplazamiento que muestra los productos adquiridos
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Ocupa el espacio restante disponible
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Cada producto se representa dentro de una tarjeta (Card)
                items(items) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(item.nombre, fontWeight = FontWeight.Bold)
                            Text("Cantidad: ${item.cantidad}")
                            Text("Subtotal: $${item.precio * item.cantidad}")
                        }
                    }
                }
            }

            // Línea divisoria entre la lista y el resumen de pago
            Divider()

            // Monto total pagado
            Text(
                "Total Pagado: $${String.format("%.2f", total)}",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )

            // Método de pago seleccionado
            Text("Método de Pago: $metodoPago", color = Color.Gray)

            // Botón para finalizar la compra y volver al inicio
            Button(
                onClick = onFinalizar,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Finalizar y volver al inicio", color = Color.White)
            }
        }
    }
}
