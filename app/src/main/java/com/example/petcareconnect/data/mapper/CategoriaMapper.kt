package com.example.petcareconnect.data.mapper

import com.example.petcareconnect.data.model.Categoria
import com.example.petcareconnect.data.remote.dto.CategoriaSimpleDto

fun CategoriaSimpleDto.toLocal(): Categoria {
    return Categoria(
        idCategoria = idCategoria,
        nombre = nombre
    )
}
