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
        )
        // ⚠ EstadoProducto ya no es una tabla → ya no se usa foreign key
    ]
)
data class Producto(

    @PrimaryKey(autoGenerate = true)
    val idProducto: Int = 0,

    val nombre: String,
    val precio: Double,
    val stock: Int,

    // Relación con Categoria
    val categoriaId: Int,

    //  Ahora se usa enum en vez de estadoId
    val estado: EstadoProducto = EstadoProducto.DISPONIBLE,

    // Imagen (opcional)
    val imagenResId: Int? = null,
    val imagenUri: String? = null      // imágenes desde cámara/galería
)
