package com.example.petcareconnect.data.mapper

import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.model.EstadoProducto
import com.example.petcareconnect.data.remote.dto.ProductoDto
import com.example.petcareconnect.utils.getDrawableProducto

fun ProductoDto.toLocal(): Producto {
    return Producto(
        idProducto = idProducto
            ?: throw IllegalStateException("El backend devolvió idProducto = null"),

        nombre = nombre,
        precio = precio,
        stock = stock,
        categoriaId = categoria.idCategoria,
        estado = EstadoProducto.valueOf(estado.uppercase()),

        // ✔ Si backend devuelve imagenUrl → usarla
        imagenUri = imagenUrl,

        // ✔ Si backend NO trae imagen → usar drawable por defecto
        imagenResId = if (imagenUrl == null) getDrawableProducto(nombre) else null
    )
}
