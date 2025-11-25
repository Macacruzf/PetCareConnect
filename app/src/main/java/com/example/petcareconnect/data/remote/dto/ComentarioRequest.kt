package com.example.petcareconnect.data.remote.dto

data class ComentarioRequest(
    val idUsuario: Long,
    val mensaje: String,
    val tipoMensaje: String // "CLIENTE" o "SOPORTE"
)
