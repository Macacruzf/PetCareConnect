package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.petcareconnect.data.remote.dto.TicketResponse
import com.example.petcareconnect.ui.viewmodel.TicketViewModel

@Composable
fun TicketScreen(
    productoId: Long,
    idUsuario: Long,
    vm: TicketViewModel
) {
    val state by vm.state.collectAsState()

    // ⭐ Reset al cambiar de producto
    LaunchedEffect(productoId) {
        vm.resetState()
        vm.loadTickets(productoId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Comentarios y Calificaciones",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        // ===============================
        // LISTA DE RESEÑAS
        // ===============================
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.tickets) { ticket ->
                TicketCard(ticket)
            }
        }

        Spacer(Modifier.height(16.dp))

        Text("Tu Opinión", style = MaterialTheme.typography.titleMedium)

        // ===============================
        // COMENTARIO PRINCIPAL DEL CLIENTE
        // ===============================
        OutlinedTextField(
            value = state.comentario,
            onValueChange = vm::onComentarioChange,
            label = { Text("Escribe tu comentario…") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        // ===============================
        // SELECCIÓN DE ESTRELLAS
        // ===============================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            (1..5).forEach { estrella ->
                IconButton(onClick = { vm.onCalificacionChange(estrella) }) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (estrella <= state.calificacion)
                            Color(0xFFFFD700)
                        else Color.LightGray
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // ===============================
        // BOTÓN ENVIAR RESEÑA
        // ===============================
        Button(
            onClick = { vm.enviarTicket(productoId, idUsuario) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar reseña")
        }

        // ===============================
        // MENSAJES
        // ===============================
        state.successMsg?.let {
            Text(it, color = Color(0xFF4CAF50), modifier = Modifier.padding(top = 8.dp))
        }

        state.errorMsg?.let {
            Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun TicketCard(ticket: TicketResponse) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(12.dp)) {

            Text(
                "Usuario #${ticket.idUsuario}",
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            // ⭐ Estrellas
            Row {
                repeat(ticket.clasificacion) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "Estrella",
                        tint = Color(0xFFFFD700)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(ticket.comentario)

            // ===============================
            // 🌟 RESPUESTA DEL ADMIN (SOPORTE)
            // ===============================
            val respuestas = ticket.comentarios.filter { it.tipoMensaje == "SOPORTE" }

            if (respuestas.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))

                respuestas.forEach { resp ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(Color(0xFFE7F1FF))
                    ) {
                        Column(Modifier.padding(10.dp)) {

                            Text(
                                "Respuesta del administrador",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A73E8)
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(resp.mensaje)
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
