package com.example.petcareconnect.data.session

object UserSession {
    var usuarioId: Int? = null
    var rol: String? = null
    var estado: String? = null   // ← ESTE ES EL CAMPO QUE FALTABA

    fun clear() {
        usuarioId = null
        rol = null
        estado = null

    }
}
