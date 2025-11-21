package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.remote.dto.TicketResponse
import com.example.petcareconnect.ui.viewmodel.TicketViewModel

@Composable
fun ProductoDetalleScreen(
    producto: Producto,
    usuarioId: Long,
    vm: TicketViewModel
) {

    val state by vm.state.collectAsState()

    // ⭐ Cargar reseñas del producto al entrar
    LaunchedEffect(producto.idProducto) {
        vm.loadTickets(producto.idProducto.toLong())
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // ------------------------------------------------------------------
        // INFORMACIÓN DEL PRODUCTO
        // ------------------------------------------------------------------
        Row(verticalAlignment = Alignment.CenterVertically) {

            producto.imagenUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier.width(120.dp).height(120.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    producto.nombre,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
                Text("Precio: $${producto.precio}")
                Text("Stock: ${producto.stock}")
            }
        }

        Spacer(Modifier.height(20.dp))
        Divider()
        Spacer(Modifier.height(20.dp))

        // ------------------------------------------------------------------
        // SECCIÓN: CALIFICAR Y COMENTAR
        // ------------------------------------------------------------------

        Text("Tu calificación", style = MaterialTheme.typography.titleMedium)

        Row(
            Modifier.fillMaxSize(),
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

        OutlinedTextField(
            value = state.comentario,
            onValueChange = vm::onComentarioChange,
            label = { Text("Escribe tu comentario...") },
            modifier = Modifier.fillMaxSize()
        )

        Button(
            onClick = {
                vm.enviarTicket(
                    idUsuario = usuarioId,
                    idProducto = producto.idProducto.toLong()
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Enviar reseña")
        }

        state.errorMsg?.let { Text(it, color = Color.Red) }
        state.successMsg?.let { Text(it, color = Color.Green) }

        Spacer(Modifier.height(20.dp))
        Divider()
        Spacer(Modifier.height(20.dp))

        // ------------------------------------------------------------------
        // LISTADO DE RESEÑAS
        // ------------------------------------------------------------------

        Text("Reseñas", style = MaterialTheme.typography.titleLarge)

        if (state.tickets.isEmpty()) {
            Text("No hay reseñas aún.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.tickets) { ticket ->
                    TicketItem(ticket)
                }
            }
        }
    }
}

@Composable
fun TicketItem(ticket: TicketResponse) {
    Card(Modifier.fillMaxSize()) {
        Column(Modifier.padding(12.dp)) {

            Row {
                repeat(ticket.clasificacion) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color.Yellow)
                }
            }

            Text(ticket.comentario)
        }
    }
}
