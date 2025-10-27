package com.example.petcareconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class Ticket(
    @PrimaryKey(autoGenerate = true) val idTicket: Int = 0,
    val idProducto: Int,
    val nombreUsuario: String,
    val comentario: String,
    val calificacion: Int, // 1–5 estrellas
    val fecha: Long = System.currentTimeMillis()
)
