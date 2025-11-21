package com.example.petcareconnect.data.mapper

import com.example.petcareconnect.data.model.Categoria
import com.example.petcareconnect.data.remote.dto.CategoriaDto

fun CategoriaDto.toLocal(): Categoria {
    return Categoria(
        idCategoria = idCategoria,
        nombre = nombre
    )
}
