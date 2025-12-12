package com.example.petcareconnect.data.remote.dto


data class UsuarioRemote(
    val idUsuario: Int,
    val nombreUsuario: String,
    val email: String,
    val telefono: String,
    val password: String?,
    val rol: String,       // ADMIN o CLIENTE
    val estado: String,    // ACTIVO, INACTIVO o SUSPENDIDO
    val fotoPerfil: ByteArray? = null
) {
    // Override equals y hashCode para ByteArray
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UsuarioRemote

        if (idUsuario != other.idUsuario) return false
        if (nombreUsuario != other.nombreUsuario) return false
        if (email != other.email) return false
        if (telefono != other.telefono) return false
        if (password != other.password) return false
        if (rol != other.rol) return false
        if (estado != other.estado) return false
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
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + rol.hashCode()
        result = 31 * result + estado.hashCode()
        result = 31 * result + (fotoPerfil?.contentHashCode() ?: 0)
        return result
    }
}
