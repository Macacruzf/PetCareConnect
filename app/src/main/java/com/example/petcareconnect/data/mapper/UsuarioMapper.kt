package com.example.petcareconnect.data.remote.dto

import com.example.petcareconnect.data.model.Usuario

fun UsuarioRemote.toLocalUsuario(): Usuario {
    return Usuario(
        idUsuario = idUsuario.toInt(),
        nombreUsuario = nombreUsuario,
        email = email,
        telefono = telefono ?: "",
        password = "", // no se guarda
        rol = rol,
        estado = estado,
        fotoUri = null
    )
}
