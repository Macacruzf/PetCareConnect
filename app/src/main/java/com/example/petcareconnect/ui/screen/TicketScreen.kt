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
import com.example.petcareconnect.data.model.Ticket
import com.example.petcareconnect.ui.viewmodel.TicketViewModel

// ---------------------------------------------------------------------------
// PANTALLA DE COMENTARIOS Y CALIFICACIONES (TicketScreen)
// ---------------------------------------------------------------------------
// Esta pantalla permite visualizar y publicar opiniones (tickets o reseñas)
// sobre un producto específico dentro de PetCare Connect.
//
// Contiene:
//  - Listado dinámico de comentarios existentes.
//  - Campo para agregar una nueva opinión y calificación (1 a 5 estrellas).
//  - Animación de recomposición al enviar o actualizar calificaciones.
// ---------------------------------------------------------------------------
@Composable
fun TicketScreen(
    productoId: Int,          // ID del producto sobre el que se opinó
    usuario: String,          // Nombre del usuario que realiza la reseña
    vm: TicketViewModel       // ViewModel encargado de la lógica y estado
) {
    // Estado reactivo del ViewModel
    val state by vm.state.collectAsState()

    // -----------------------------------------------------------------------
    // EFECTO DE CARGA INICIAL
    // -----------------------------------------------------------------------
    // Se ejecuta una sola vez al ingresar, cargando los comentarios asociados
    // al producto indicado. Usa `LaunchedEffect` para evitar recargas innecesarias.
    // -----------------------------------------------------------------------
    LaunchedEffect(productoId) {
        vm.loadTickets(productoId)
    }

    // -----------------------------------------------------------------------
    // CONTENEDOR PRINCIPAL
    // -----------------------------------------------------------------------
    // Estructura vertical con márgenes y separación entre elementos.
    // Incluye encabezado, listado, formulario y mensajes de estado.
    // -----------------------------------------------------------------------
    Column(Modifier.padding(16.dp)) {

        // Título general
        Text(
            "Comentarios y calificaciones",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(8.dp))

        // -------------------------------------------------------------------
        // LISTADO DE COMENTARIOS EXISTENTES
        // -------------------------------------------------------------------
        // Usa LazyColumn para mostrar los tickets de forma eficiente.
        // Cada comentario se representa mediante el componente `TicketCard`.
        // La recomposición es animada automáticamente cuando cambia el estado.
        // -------------------------------------------------------------------
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.tickets) { ticket ->
                TicketCard(ticket)
            }
        }

        Spacer(Modifier.height(16.dp))

        // -------------------------------------------------------------------
        // FORMULARIO DE NUEVA OPINIÓN
        // -------------------------------------------------------------------
        // Sección inferior donde el usuario puede escribir un comentario y
        // asignar una calificación entre 1 y 5 estrellas.
        // -------------------------------------------------------------------
        Text("Tu opinión", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = state.comentario,
            onValueChange = vm::onComentarioChange,
            label = { Text("Escribe tu comentario...") },
            modifier = Modifier.fillMaxWidth()
        )

        // -------------------------------------------------------------------
        // SELECCIÓN DE ESTRELLAS (CALIFICACIÓN)
        // -------------------------------------------------------------------
        // Al presionar una estrella, se actualiza el valor en el ViewModel.
        // Compose anima el cambio de color mediante recomposición.
        // -------------------------------------------------------------------
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

        // -------------------------------------------------------------------
        // BOTÓN DE ENVÍO
        // -------------------------------------------------------------------
        // Envía el comentario y calificación al ViewModel.
        // El botón usa los colores por defecto de Material 3.
        // -------------------------------------------------------------------
        Button(
            onClick = { vm.enviarTicket(productoId, usuario) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar")
        }

        // -------------------------------------------------------------------
        // MENSAJES DE ESTADO
        // -------------------------------------------------------------------
        // Muestra texto verde cuando el envío fue exitoso, o rojo en caso
        // de error. La visibilidad depende del estado actual del ViewModel.
        // -------------------------------------------------------------------
        state.successMsg?.let {
            Text(it, color = Color.Green)
        }

        state.errorMsg?.let {
            Text(it, color = Color.Red)
        }
    }
}

// ---------------------------------------------------------------------------
// COMPONENTE VISUAL DE COMENTARIO (TicketCard)
// ---------------------------------------------------------------------------
// Representa un único comentario en una tarjeta.
// Muestra el nombre del usuario, la cantidad de estrellas y el texto.
//
// Animaciones implícitas:
//  - Las estrellas cambian suavemente de color (gracias a recomposición).
//  - La tarjeta se inserta o elimina con transición natural en LazyColumn.
// ---------------------------------------------------------------------------
@Composable
fun TicketCard(ticket: Ticket) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(8.dp)) {
            // Nombre del usuario (negrita)
            Text(ticket.nombreUsuario, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)

            // Filas de estrellas según calificación
            Row {
                repeat(ticket.calificacion) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color.Yellow)
                }
            }

            // Comentario del usuario
            Text(ticket.comentario)
        }
    }
}
