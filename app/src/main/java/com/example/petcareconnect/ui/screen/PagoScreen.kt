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
 * PANTALLA DE SELECCIÓN DE MÉTODO DE PAGO
 * ---------------------------------------------------------
 * Muestra:
 *  - Total a pagar
 *  - Botón de "Efectivo / Transferencia"
 *  - Botón de "Tarjeta de Débito / Crédito"
 *
 * Se conecta con el estado REAL del carrito.
 */
@Composable
fun PagoScreen(
    carritoViewModel: CarritoViewModel,           // ViewModel del carrito
    onEfectivoOTransferencia: () -> Unit,         // Acción para pago en tienda
    onTarjeta: () -> Unit                         // Acción para pago con tarjeta
) {
    // Observa el estado actual del carrito
    val state by carritoViewModel.state.collectAsState()

    // Contenedor principal centrado
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Título
            Text(
                "Método de Pago",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(12.dp))

            // Total a pagar
            Text(
                text = "Total a pagar: $${String.format("%.2f", state.total)}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(24.dp))

            // Botón efectivo/transferencia
            Button(
                onClick = onEfectivoOTransferencia,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
            ) {
                Text("Efectivo / Transferencia", color = Color.Black)
            }

            Spacer(Modifier.height(12.dp))

            // Botón tarjeta
            Button(
                onClick = onTarjeta,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text("Tarjeta de Débito / Crédito", color = Color.White)
            }
        }
    }
}
