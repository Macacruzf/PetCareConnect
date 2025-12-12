package com.example.petcareconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val idUsuario: Int = 0,
    val nombreUsuario: String,
    val email: String,
    val telefono: String,
    val password: String,
    val rol: String,
    val estado: String,
    val fotoUri: String? = null,
    val fotoPerfil: ByteArray? = null  // Para almacenar la imagen desde el servidor
) {
    // Override equals y hashCode porque ByteArray no se compara por contenido
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Usuario

        if (idUsuario != other.idUsuario) return false
        if (nombreUsuario != other.nombreUsuario) return false
        if (email != other.email) return false
        if (telefono != other.telefono) return false
        if (password != other.password) return false
        if (rol != other.rol) return false
        if (estado != other.estado) return false
        if (fotoUri != other.fotoUri) return false
        if (fotoPerfil != null) {
            if (other.fotoPerfil == null) return false
            if (!fotoPerfil.contentEquals(other.fotoPerfil)) return false
        } else if (other.fotoPerfil != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = idUsuario
        result = 31 * result + nombreUsuario.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + telefono.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + rol.hashCode()
        result = 31 * result + estado.hashCode()
        result = 31 * result + (fotoUri?.hashCode() ?: 0)
        result = 31 * result + (fotoPerfil?.contentHashCode() ?: 0)
        return result
    }
}
