package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.petcareconnect.ui.viewmodel.CarritoViewModel

/*
 * Pantalla de selección de método de pago.
 * Muestra el total a pagar y ofrece dos opciones:
 * - Efectivo o transferencia (pago en tienda)
 * - Tarjeta de débito o crédito (pago simulado)
 */
@Composable
fun PagoScreen(
    carritoViewModel: CarritoViewModel,           // ViewModel que contiene el estado del carrito
    onEfectivoOTransferencia: () -> Unit,         // Acción al elegir pago en efectivo o transferencia
    onTarjeta: () -> Unit                         // Acción al elegir pago con tarjeta
) {
    // Observa en tiempo real el estado del carrito (productos, total, etc.)
    val state by carritoViewModel.state.collectAsState()

    // Contenedor principal centrado
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Estructura vertical de la interfaz
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Título principal
            Text(
                "Método de Pago",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(12.dp))

            // Texto que muestra el total calculado del carrito
            Text(
                text = "Total a pagar: $${String.format("%.2f", state.total)}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(24.dp))

            // Botón para seleccionar pago en tienda (efectivo o transferencia)
            Button(
                onClick = onEfectivoOTransferencia,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)) // Amarillo
            ) {
                Text("Efectivo / Transferencia", color = Color.Black)
            }

            Spacer(Modifier.height(12.dp))

            // Botón para seleccionar pago con tarjeta
            Button(
                onClick = onTarjeta,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)) // Azul
            ) {
                Text("Tarjeta de Débito / Crédito", color = Color.White)
            }
        }
    }
}
