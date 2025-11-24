package com.example.petcareconnect.data.remote.dto

data class UsuarioRemote(
    val idUsuario: Long,
    val nombreUsuario: String,
    val email: String,
    val telefono: String,
    val rol: String,
    val foto: String?          // ✔ foto opcional
)
