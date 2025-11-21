package com.example.petcareconnect.data.remote.dto


data class ProductoDto(
    val idProducto: Long,
    val nombre: String,
    val precio: Double,
    val stock: Int,
    val estado: String,
    val categoria: CategoriaDto,
    val imagenUrl: String? = null
)
