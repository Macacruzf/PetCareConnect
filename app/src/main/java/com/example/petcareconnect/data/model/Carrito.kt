package com.example.petcareconnect.data.model

import kotlin.random.Random

/**
 * Modelo del item dentro del carrito.
 * Cada item debe tener un ID ÚNICO (idItem) para permitir
 * que las cantidades se modifiquen de manera independiente.
 */
data class Carrito(

    // ID interno del item en el carrito (NO es el producto)
    val idItem: Int = Random.nextInt(),

    // Identificador real del producto desde la BD
    val idProducto: Long,

    val nombre: String,
    val precio: Double,
    val cantidad: Int = 1,

    // ⭐ NECESARIO PARA EVITAR PASARSE DEL STOCK
    val stock: Int,

    // URL de la imagen desde el backend
    val imagenUrl: String? = null,

    // URI local (cámara/galería) - solo para edición temporal
    val imagenUri: String? = null,
    
    // ID de drawable local como fallback
    val imagenResId: Int? = null
)
