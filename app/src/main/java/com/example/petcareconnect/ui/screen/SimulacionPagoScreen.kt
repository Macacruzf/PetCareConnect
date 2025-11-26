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
import com.example.petcareconnect.ui.viewmodel.ProductoViewModel
import kotlinx.coroutines.delay

@Composable
fun SimulacionPagoScreen(
    carritoViewModel: CarritoViewModel,
    productoViewModel: ProductoViewModel,
    onPagoExitoso: () -> Unit
) {

    var procesando by remember { mutableStateOf(false) }
    var iniciarPago by remember { mutableStateOf(false) }

    val carritoState by carritoViewModel.state.collectAsState()

    // -----------------------------------------------------------------------
    // EFECTO DE PROCESAMIENTO (2.5s) + DESCUENTO DE STOCK + GUARDAR DETALLE
    // -----------------------------------------------------------------------
    if (iniciarPago) {
        LaunchedEffect(Unit) {
            delay(2500)

            // ⭐ DESCONTAR STOCK DEL BACKEND
            carritoState.items.forEach { item ->
                productoViewModel.descontarStock(
                    idProducto = item.idProducto,
                    cantidad = item.cantidad
                )
            }

            // ⭐ GUARDAR INFO DE LA COMPRA (ANTES de vaciar)
            carritoViewModel.compraItemsTemp = carritoState.items
            carritoViewModel.compraTotalTemp = carritoState.total
            carritoViewModel.compraMetodoTemp = "Tarjeta"

            // ⭐ Vaciar carrito (después de guardar)
            carritoViewModel.vaciarCarrito()

            // ⭐ Continuar flujo
            onPagoExitoso()
        }
    }

    // -----------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        if (procesando) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                CircularProgressIndicator()

                Spacer(Modifier.height(12.dp))

                Text("Procesando pago...", color = Color.Gray)
            }

        } else {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    "Pago con Tarjeta",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        procesando = true
                        iniciarPago = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text("Pagar ahora", color = Color.White)
                }
            }
        }
    }
}
