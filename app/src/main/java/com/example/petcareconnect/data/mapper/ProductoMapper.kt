package com.example.petcareconnect.data.mapper

import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.model.EstadoProducto
import com.example.petcareconnect.data.remote.dto.ProductoDto
import com.example.petcareconnect.utils.getDrawableProducto

fun ProductoDto.toLocal(): Producto {
    return Producto(
        idProducto = idProducto.toLong(),
        nombre = nombre,
        precio = precio,
        stock = stock,
        categoriaId = categoria.idCategoria.toLong(),
        estado = EstadoProducto.valueOf(estado),
        imagenResId = getDrawableProducto(nombre),
        imagenUri = imagenUrl
    )
}
