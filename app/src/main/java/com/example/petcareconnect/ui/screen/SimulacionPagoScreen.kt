package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.petcareconnect.domain.validation.*
import com.example.petcareconnect.ui.theme.PetGreenPrimary
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

    // ✅ Estados del formulario de tarjeta
    var numeroTarjeta by remember { mutableStateOf("") }
    var nombreTitular by remember { mutableStateOf("") }
    var fechaExpiracion by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    // ✅ Errores de validación
    var errorNumero by remember { mutableStateOf<String?>(null) }
    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorFecha by remember { mutableStateOf<String?>(null) }
    var errorCVV by remember { mutableStateOf<String?>(null) }
    var errorGeneral by remember { mutableStateOf<String?>(null) }

    val carritoState by carritoViewModel.state.collectAsState()

    // ✅ Validar formulario completo
    fun validarFormulario(): Boolean {
        errorNumero = validateCardNumber(numeroTarjeta)
        errorNombre = validateCardHolderName(nombreTitular)
        errorFecha = validateExpiryDate(fechaExpiracion)
        errorCVV = validateCVV(cvv)

        return errorNumero == null && errorNombre == null &&
               errorFecha == null && errorCVV == null
    }

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

            // ⭐ Continuar flujo
            onPagoExitoso()
        }
    }

    // -----------------------------------------------------------------------
    // UI
    // -----------------------------------------------------------------------
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        if (procesando) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                CircularProgressIndicator(color = PetGreenPrimary)

                Spacer(Modifier.height(12.dp))

                Text("Procesando pago seguro...", color = Color.Gray)

                Spacer(Modifier.height(8.dp))

                Text("Por favor espera", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

        } else {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "Pago con Tarjeta",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    color = PetGreenPrimary
                )

                Spacer(Modifier.height(24.dp))

                // ✅ NÚMERO DE TARJETA
                OutlinedTextField(
                    value = numeroTarjeta,
                    onValueChange = {
                        // Formatear automáticamente: agregar espacio cada 4 dígitos
                        val cleaned = it.replace(" ", "").take(16)
                        numeroTarjeta = cleaned.chunked(4).joinToString(" ")
                        errorNumero = null
                        errorGeneral = null
                    },
                    label = { Text("Número de tarjeta") },
                    placeholder = { Text("1234 5678 9012 3456") },
                    singleLine = true,
                    isError = errorNumero != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorNumero != null) {
                    Text(
                        errorNumero!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                // ✅ NOMBRE DEL TITULAR
                OutlinedTextField(
                    value = nombreTitular,
                    onValueChange = {
                        nombreTitular = it.uppercase()
                        errorNombre = null
                        errorGeneral = null
                    },
                    label = { Text("Nombre del titular") },
                    placeholder = { Text("JUAN PÉREZ") },
                    singleLine = true,
                    isError = errorNombre != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorNombre != null) {
                    Text(
                        errorNombre!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // ✅ FECHA DE EXPIRACIÓN
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = fechaExpiracion,
                            onValueChange = {
                                // Formatear automáticamente MM/AA
                                val cleaned = it.replace("/", "").take(4)
                                fechaExpiracion = if (cleaned.length > 2) {
                                    "${cleaned.substring(0, 2)}/${cleaned.substring(2)}"
                                } else {
                                    cleaned
                                }
                                errorFecha = null
                                errorGeneral = null
                            },
                            label = { Text("Fecha") },
                            placeholder = { Text("MM/AA") },
                            singleLine = true,
                            isError = errorFecha != null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (errorFecha != null) {
                            Text(
                                errorFecha!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                    }

                    // ✅ CVV
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = cvv,
                            onValueChange = {
                                if (it.length <= 4) {
                                    cvv = it.filter { char -> char.isDigit() }
                                    errorCVV = null
                                    errorGeneral = null
                                }
                            },
                            label = { Text("CVV") },
                            placeholder = { Text("123") },
                            singleLine = true,
                            isError = errorCVV != null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (errorCVV != null) {
                            Text(
                                errorCVV!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ✅ TOTAL A PAGAR
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total a pagar:", fontWeight = FontWeight.Bold)
                        Text(
                            "$${String.format("%.2f", carritoState.total)}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge,
                            color = PetGreenPrimary
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ✅ ERROR GENERAL
                if (errorGeneral != null) {
                    Text(
                        errorGeneral!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // ✅ BOTÓN PAGAR
                Button(
                    onClick = {
                        if (validarFormulario()) {
                            procesando = true
                            iniciarPago = true
                        } else {
                            errorGeneral = "Por favor corrige los errores del formulario"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PetGreenPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Pagar ahora", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
