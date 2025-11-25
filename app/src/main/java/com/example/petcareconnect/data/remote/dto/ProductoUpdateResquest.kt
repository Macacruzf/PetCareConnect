package com.example.petcareconnect.data.remote.dto


data class ProductoUpdateRequest(
    val nombre: String,
    val precio: Double,
    val stock: Int,
    val estado: String,
    val categoria: CategoriaSimpleDto
)

