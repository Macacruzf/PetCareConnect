package com.example.petcareconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val email: String,
    val telefono: String,
    val password: String,
    val rol: String // puede ser "admin" o "cliente"
)