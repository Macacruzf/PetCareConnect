package com.example.petcareconnect.data.remote.dto

data class TicketRequest(
    val idUsuario: Long,
    val idProducto: Long,
    val clasificacion: Int,
    val comentario: String
)
