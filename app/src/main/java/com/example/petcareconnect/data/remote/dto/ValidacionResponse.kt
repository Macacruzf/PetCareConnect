package com.example.petcareconnect.data.remote.dto

data class ValidacionResponse(
    val valido: Boolean,
    val idUsuario: Int?,
    val rol: String?,
    val estado: String?
)
