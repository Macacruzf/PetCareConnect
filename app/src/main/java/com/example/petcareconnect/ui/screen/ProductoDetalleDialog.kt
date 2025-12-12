package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.petcareconnect.R
import com.example.petcareconnect.data.model.EstadoProducto
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.remote.dto.TicketResponse
import com.example.petcareconnect.ui.viewmodel.TicketViewModel

/* ============================================================
   DIALOGO DETALLE DEL PRODUCTO — COMPLETO + SCROLL
============================================================ */

@Composable
fun ProductoDetalleDialog(
    producto: Producto,
    usuarioId: Long,
    esAdmin: Boolean,
    vmTicket: TicketViewModel,

    onAgregar: (Producto) -> Unit,
    onEditar: (Producto) -> Unit,
    onEliminar: (Producto) -> Unit,
    onCambiarEstado: (EstadoProducto) -> Unit,
    onCerrar: () -> Unit
) {

    val state by vmTicket.state.collectAsState()
    var mostrarEliminar by remember { mutableStateOf(false) }

    LaunchedEffect(producto.idProducto) {
        vmTicket.resetState()
        vmTicket.loadTickets(producto.idProducto.toLong())
    }

    Dialog(onDismissRequest = onCerrar) {

        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f),  // ⭐ para que todo quepa en pantalla
            shape = RoundedCornerShape(18.dp)
        ) {

            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())  // ⭐ HABILITAR SCROLL
            ) {

                /* CERRAR */
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onCerrar) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                /* IMAGEN - ✅ PRIORIDAD: imagenUrl (backend) > imagenUri (local) > imagenResId (drawable) > logo default */
                val painter = when {
                    producto.imagenUrl != null -> rememberAsyncImagePainter(producto.imagenUrl)
                    producto.imagenUri != null -> rememberAsyncImagePainter(producto.imagenUri)
                    producto.imagenResId != null -> painterResource(id = producto.imagenResId)
                    else -> painterResource(id = R.drawable.ic_petcare_logo)
                }

                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    producto.nombre,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text(
                    "$${producto.precio}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(10.dp))

                EstadoBadge(producto.estado)

                Spacer(Modifier.height(20.dp))


                /* ============================================================
                   BOTONES (CLIENTE / ADMIN)
                ============================================================ */

                if (!esAdmin) {
                    val puedeAgregar = producto.estado == EstadoProducto.DISPONIBLE && producto.stock > 0

                    Button(
                        onClick = { if (puedeAgregar) onAgregar(producto) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = puedeAgregar,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Icon(Icons.Default.ShoppingCart, null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Agregar al carrito", color = Color.White)
                    }
                }

                if (esAdmin) {

                    OutlinedButton(
                        onClick = { onEditar(producto) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Edit, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Editar")
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { mostrarEliminar = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, null, tint = Color.Red)
                        Spacer(Modifier.width(6.dp))
                        Text("Eliminar", color = Color.Red)
                    }

                    Spacer(Modifier.height(12.dp))

                    val nuevoEstado =
                        if (producto.estado == EstadoProducto.DISPONIBLE)
                            EstadoProducto.NO_DISPONIBLE
                        else EstadoProducto.DISPONIBLE

                    Button(
                        onClick = { onCambiarEstado(nuevoEstado) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF395BBC))
                    ) {
                        Text(
                            if (producto.estado == EstadoProducto.DISPONIBLE) "Desactivar" else "Activar",
                            color = Color.White
                        )
                    }
                }


                Spacer(Modifier.height(20.dp))
                Divider()
                Spacer(Modifier.height(20.dp))


                /* ============================================================
                   RESEÑAR PRODUCTO
                ============================================================ */

                Text("Tu calificación", style = MaterialTheme.typography.titleMedium)

                if (!esAdmin) {

                    // Estrellas
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        (1..5).forEach { star ->
                            IconButton(onClick = { vmTicket.onCalificacionChange(star) }) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "Estrella",
                                    tint = if (star <= state.calificacion) Color(0xFFFFD700) else Color.LightGray
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = state.comentario,
                        onValueChange = vmTicket::onComentarioChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Escribe tu comentario…") }
                    )

                    Button(
                        onClick = {
                            vmTicket.enviarTicket(
                                idUsuario = usuarioId,
                                idProducto = producto.idProducto.toLong()
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Enviar reseña")
                    }

                    state.errorMsg?.let { Text(it, color = Color.Red) }
                    state.successMsg?.let { Text(it, color = Color.Green) }
                }


                Spacer(Modifier.height(20.dp))
                Divider()
                Spacer(Modifier.height(12.dp))


                /* ============================================================
                   LISTA DE RESEÑAS (SE VE COMPLETO)
                ============================================================ */

                Text("Reseñas", style = MaterialTheme.typography.titleLarge)

                if (state.tickets.isEmpty()) {
                    Text("No hay reseñas aún.")
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        state.tickets.forEach { t ->
                            TicketReviewItem(
                                ticket = t,
                                esAdmin = esAdmin,
                                vmTicket = vmTicket
                            )
                        }
                    }
                }
            }
        }
    }


    /* ============================================================
       DIALOGO CONFIRMACIÓN DE ELIMINAR
    ============================================================ */
    if (mostrarEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarEliminar = false },
            title = { Text("Eliminar producto") },
            text = { Text("¿Estás seguro de que deseas eliminar este producto?") },

            confirmButton = {
                Button(
                    onClick = {
                        mostrarEliminar = false
                        onEliminar(producto)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },

            dismissButton = {
                TextButton(onClick = { mostrarEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/* ============================================================
   BADGE ESTADO PRODUCTO
============================================================ */

@Composable
fun EstadoBadge(estado: EstadoProducto) {

    val (texto, color) = when (estado) {
        EstadoProducto.DISPONIBLE -> "DISPONIBLE" to Color(0xFF4CAF50)
        EstadoProducto.SIN_STOCK -> "SIN STOCK" to Color(0xFFFF3B3B)
        EstadoProducto.NO_DISPONIBLE -> "NO DISPONIBLE" to Color(0xFFFF9800)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(color.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(texto, color = color, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/* ============================================================
   ITEM DE RESEÑA (CLIENTE + RESPUESTA ADMIN)
============================================================ */

@Composable
fun TicketReviewItem(
    ticket: TicketResponse,
    esAdmin: Boolean,
    vmTicket: TicketViewModel
) {

    var respuesta by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {

        Column(Modifier.padding(12.dp)) {

            // Estrellas
            Row {
                repeat(ticket.clasificacion) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "Estrella",
                        tint = Color(0xFFFFD700)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Text(ticket.comentario)

            val respuestasAdmin = ticket.comentarios.filter { it.tipoMensaje == "SOPORTE" }

            if (respuestasAdmin.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))

                respuestasAdmin.forEach { resp ->
                    Card(
                        colors = CardDefaults.cardColors(Color(0xFFE5F1FF)),
                        modifier = Modifier.fillMaxWidth()
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

            if (esAdmin) {

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = respuesta,
                    onValueChange = { respuesta = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Responder al cliente…") }
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (respuesta.isNotBlank()) {
                            vmTicket.onNuevoComentarioChange(respuesta)
                            vmTicket.agregarComentario(
                                idTicket = ticket.idTicket,
                                idUsuario = ticket.idUsuario
                            )
                            respuesta = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Responder como administrador")
                }
            }
        }
    }
}
