package com.example.petcareconnect.data.remote.dto


data class TicketResponse(
    val idTicket: Long,
    val fechaCreacion: String,
    val idUsuario: Long,
    val idProducto: Long,
    val clasificacion: Int,
    val comentario: String
)
