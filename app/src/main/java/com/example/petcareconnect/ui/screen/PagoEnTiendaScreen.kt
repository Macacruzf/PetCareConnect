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
 * Informa al usuario que el pedido fue recibido y le permite continuar para ver el detalle de la venta.
 */
@Composable
fun PagoEnTiendaScreen(onContinuar: () -> Unit) {
    // Contenedor principal centrado en toda la pantalla
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Disposición vertical de los elementos
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // Título principal
            Text(
                "Pago en Tienda",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF4CAF50) // Verde corporativo de PetCare Connect
            )

            Spacer(Modifier.height(12.dp))

            // Mensaje informativo al usuario
            Text(
                "Tu pedido fue registrado. Por favor realiza el pago en la tienda para completar tu compra.",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 24.dp),
            )

            Spacer(Modifier.height(20.dp))

            // Botón que permite continuar al detalle de venta
            Button(
                onClick = onContinuar, // Acción de navegación
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Ver detalle de la venta", color = Color.White)
            }
        }
    }
}
