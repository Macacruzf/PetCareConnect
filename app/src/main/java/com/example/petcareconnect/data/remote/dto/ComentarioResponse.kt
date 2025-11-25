package com.example.petcareconnect.data.remote.dto

data class ComentarioResponse(
    val idComentario: Long,
    val idUsuario: Long,
    val mensaje: String,
    val fecha: String,
    val tipoMensaje: String
)
