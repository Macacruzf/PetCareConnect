package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/*
 * Pantalla que se muestra después de registrar un pedido con método de pago en tienda.
 * Informa al usuario que el pedido fue recibido y permite continuar al detalle de venta.
 */
@Composable
fun PagoEnTiendaScreen(onContinuar: () -> Unit) {

    // Contenedor principal centrado
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Título principal
            Text(
                text = "Pago en Tienda",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF4CAF50)
            )

            Spacer(Modifier.height(12.dp))

            // Mensaje informativo
            Text(
                text = "Tu pedido fue registrado. Por favor realiza el pago en la tienda para completar tu compra.",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 24.dp),
            )

            Spacer(Modifier.height(20.dp))

            // Botón para continuar
            Button(
                onClick = onContinuar,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Ver detalle de la venta", color = Color.White)
            }
        }
    }
}
