package com.example.petcareconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ventas")
data class Venta(
    @PrimaryKey(autoGenerate = true) val idVenta: Int = 0,
    val fecha: String,
    val cliente: String,
    val total: Double,
    val metodoPago: String = "Desconocido" // 🔹 nuevo campo agregado
)
