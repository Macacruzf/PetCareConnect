package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// ---------------------------------------------------------------------------
// PANTALLA DE SIMULACIÓN DE PAGO
// ---------------------------------------------------------------------------
// Esta pantalla simula el proceso de pago con tarjeta dentro de la aplicación.
// Utiliza animaciones y efectos visuales de Material 3 para representar el
// procesamiento de un pago en tiempo real.
//
// Flujo del componente:
// 1. El usuario presiona el botón "Pagar ahora".
// 2. Se muestra un indicador de carga (animación de progreso).
// 3. Después de 2,5 segundos, se llama a la función onPagoExitoso().
// ---------------------------------------------------------------------------
@Composable
fun SimulacionPagoScreen(onPagoExitoso: () -> Unit) {

    // Estado que controla si se está procesando el pago.
    var procesando by remember { mutableStateOf(false) }

    // Estado que activa el efecto de retardo y ejecución del pago simulado.
    var iniciarPago by remember { mutableStateOf(false) }

    // -----------------------------------------------------------------------
    // EFECTO DE PROCESAMIENTO (ANIMACIÓN TEMPORAL)
    // -----------------------------------------------------------------------
    // Este bloque se ejecuta solo cuando el usuario presiona el botón de pago.
    // Usa un delay de 2,5 segundos para simular el tiempo real de procesamiento.
    // Al finalizar, se ejecuta la acción de "pago exitoso".
    // -----------------------------------------------------------------------
    if (iniciarPago) {
        LaunchedEffect(Unit) {
            delay(2500) // Tiempo simulado de procesamiento del pago
            onPagoExitoso() // Acción que se dispara al finalizar
        }
    }

    // -----------------------------------------------------------------------
    // CONTENEDOR PRINCIPAL DE LA PANTALLA
    // -----------------------------------------------------------------------
    // Se centra vertical y horizontalmente todo el contenido dentro de la Box.
    // -----------------------------------------------------------------------
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        // Si el estado indica que se está procesando el pago,
        // se muestra el indicador de progreso circular (animación continua).
        if (procesando) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Animación visual que gira indefinidamente (Material3)
                CircularProgressIndicator()

                Spacer(Modifier.height(8.dp))

                // Texto informativo con color tenue
                Text("Procesando pago...", color = Color.Gray)
            }
        } else {
            // Si el pago aún no comenzó, se muestra la interfaz inicial.
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // Título principal del módulo
                Text("Pago con Tarjeta", fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(12.dp))

                // Botón principal que inicia el proceso de simulación.
                Button(
                    onClick = {
                        procesando = true  // Cambia el estado visual
                        iniciarPago = true // Activa el efecto LaunchedEffect
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3) // Azul institucional
                    )
                ) {
                    Text("Pagar ahora", color = Color.White)
                }
            }
        }
    }
}
