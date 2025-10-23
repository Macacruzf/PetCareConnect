package com.example.petcareconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "ventas")
data class Venta(
    @PrimaryKey(autoGenerate = true) val idVenta: Int = 0,
    val fecha: Long = System.currentTimeMillis(),
    val cliente: String,
    val total: Double
)