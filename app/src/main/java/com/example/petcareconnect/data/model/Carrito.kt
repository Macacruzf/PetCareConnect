package com.example.petcareconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carrito")
data class Carrito {
    @PrimaryKey(autoGenerate = true) val idItem: Int = 0,
    val idProducto: Int,
    val nombre: String,
    val precio: Double,
    val cantidad: Int,
    val imagenResId: Int? = null
}