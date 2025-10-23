package com.example.petcareconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "tickets",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],       // ✅ debe coincidir con Usuario.id
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["usuarioId"])] // ✅ mejora rendimiento
)
data class Ticket(
    @PrimaryKey(autoGenerate = true) val idTicket: Int = 0,
    val usuarioId: Int,
    val tipo: String,       // "Reclamo", "Felicitación", "Sugerencia"
    val comentario: String,
    val fecha: String,
    val estado: String = "Pendiente"
)
