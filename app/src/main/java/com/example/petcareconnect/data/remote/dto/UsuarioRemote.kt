package com.example.petcareconnect.data.remote.dto


data class UsuarioRemote(
    val idUsuario: Int,
    val nombreUsuario: String,
    val email: String,
    val telefono: String,
    val password: String?,
    val rol: String,       // ADMIN o CLIENTE
    val estado: String     // ACTIVO, INACTIVO o SUSPENDIDO
)
