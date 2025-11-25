package com.example.petcareconnect.data.remote

import com.example.petcareconnect.data.mapper.toLocal
import com.example.petcareconnect.data.model.Categoria
import com.example.petcareconnect.data.remote.api.CategoriaApi
import com.example.petcareconnect.data.remote.dto.CategoriaRequest

class CategoriaRemoteRepository(
    private val api: CategoriaApi
) {

    suspend fun getCategoriasRemotas(): List<Categoria> {
        return api.obtenerCategorias()
            .map { it.toLocal() }
    }

    suspend fun crearCategoria(nombre: String): Categoria {
        val body = CategoriaRequest(nombre)
        return api.crearCategoria(body).toLocal()
    }

    suspend fun actualizarCategoria(id: Long, nombre: String): Categoria {
        val body = CategoriaRequest(nombre)
        return api.actualizarCategoria(id, body).toLocal()
    }

    suspend fun eliminarCategoria(id: Long) {
        api.eliminarCategoria(id)
    }
}
