package com.example.petcareconnect.data.model

import kotlin.random.Random

/**
 * Modelo del item dentro del carrito.
 * Cada item debe tener un ID ÚNICO (idItem) para permitir
 * que las cantidades se modifiquen de manera independiente.
 */
data class Carrito(

    // ID interno del item en el carrito (NO es el producto)
    val idItem: Int = Random.nextInt(),   // esto evita que 2 productos se actualicen juntos

    // Identificador real del producto desde la BD
    val idProducto: Long,

    val nombre: String,
    val precio: Double,
    val cantidad: Int = 1,

    // Imagen opcional
    val imagenResId: Int? = null,

    // Si usas imágenes desde URI en el futuro
    val imagenUri: String? = null
)
