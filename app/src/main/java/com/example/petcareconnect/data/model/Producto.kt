package com.example.petcareconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "productos",
    foreignKeys = [
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["idCategoria"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Estado::class,
            parentColumns = ["idEstado"],
            childColumns = ["estadoId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Producto(
    @PrimaryKey(autoGenerate = true) val idProducto: Int = 0,
    val nombre: String,
    val precio: Double,
    val stock: Int,
    val categoriaId: Int,
    val estadoId: Int,
    val imagenResId: Int? = null // ← Ruta o URI de la imagen (String)
)
