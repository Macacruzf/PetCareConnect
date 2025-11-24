package com.example.petcareconnect.data.remote.dto

data class RegisterRequest(
    val nombreUsuario: String,
    val email: String,
    val telefono: String,
    val password: String,
    val rol: String = "CLIENTE"
)
