package com.example.petcareconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(tableName = "productos")
data class Producto(

    @PrimaryKey(autoGenerate = true)
    val idProducto: Long = 0,

    val nombre: String,
    val precio: Double,
    val stock: Int,

    val categoriaId: Long,

    val estado: EstadoProducto = EstadoProducto.DISPONIBLE,
    
    val imagenUrl: String? = null,
    
    // URI local (cámara/galería) - solo para edición temporal
    val imagenUri: String? = null,
    
    // ID de drawable local como fallback (deprecated - usar imagenUrl)
    val imagenResId: Int? = null
)
