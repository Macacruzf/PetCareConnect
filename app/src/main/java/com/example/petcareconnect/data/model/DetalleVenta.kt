package com.example.petcareconnect.data.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "detalle_ventas",
    foreignKeys = [
        ForeignKey(
            entity = Venta::class,
            parentColumns = ["idVenta"],
            childColumns = ["ventaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["idProducto"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DetalleVenta(
    @PrimaryKey(autoGenerate = true) val idDetalle: Int = 0,
    val ventaId: Int,
    val productoId: Int,
    val nombre: String,
    val cantidad: Int,
    val subtotal: Double
)