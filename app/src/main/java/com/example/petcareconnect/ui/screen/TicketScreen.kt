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
import androidx.compose.ui.unit.dp
import com.example.petcareconnect.data.remote.dto.TicketResponse
import com.example.petcareconnect.ui.viewmodel.TicketViewModel

@Composable
fun TicketScreen(
    productoId: Long,          // ID del producto (Long)
    idUsuario: Long,           // ID del usuario autenticado
    vm: TicketViewModel        // ViewModel de reseñas
) {
    val state by vm.state.collectAsState()

    // Cargar reseñas del producto al entrar
    LaunchedEffect(productoId) {
        vm.loadTickets(productoId)
    }

    Column(Modifier.padding(16.dp)) {

        Text(
            "Comentarios y calificaciones",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(8.dp))

        // ---------------- LISTA DE RESEÑAS ----------------
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.tickets) { ticket ->
                TicketCard(ticket)
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---------------- FORMULARIO ----------------
        Text("Tu opinión", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = state.comentario,
            onValueChange = vm::onComentarioChange,
            label = { Text("Escribe tu comentario...") },
            modifier = Modifier.fillMaxWidth()
        )

        // ---------------- ESTRELLAS ----------------
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            (1..5).forEach { estrella ->
                IconButton(onClick = { vm.onCalificacionChange(estrella) }) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (estrella <= state.calificacion) Color.Yellow else Color.Gray
                    )
                }
            }
        }

        // ---------------- BOTÓN ENVIAR ----------------
        Button(
            onClick = { vm.enviarTicket(productoId, idUsuario) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar")
        }

        // ---------------- MENSAJES ----------------
        state.successMsg?.let {
            Text(it, color = Color.Green)
        }
        state.errorMsg?.let {
            Text(it, color = Color.Red)
        }
    }
}

@Composable
fun TicketCard(ticket: TicketResponse) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(8.dp)) {

            Text(
                "Usuario ID: ${ticket.idUsuario}",
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )

            // estrellas
            Row {
                repeat(ticket.clasificacion) {
                    Icon(Icons.Filled.Star, null, tint = Color.Yellow)
                }
            }

            Text(ticket.comentario)
        }
    }
}
