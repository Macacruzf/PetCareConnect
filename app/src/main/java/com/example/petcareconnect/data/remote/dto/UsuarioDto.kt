package com.example.petcareconnect.data.remote.dto

data class UsuarioDto(
    val idUsuario: Int,
    val nombreUsuario: String,
    val email: String,
    val telefono: String,
    val rol: String,
    val estado: String
)
