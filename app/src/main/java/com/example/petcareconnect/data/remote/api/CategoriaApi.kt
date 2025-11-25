package com.example.petcareconnect.data.remote.api

import com.example.petcareconnect.data.remote.dto.CategoriaRequest
import com.example.petcareconnect.data.remote.dto.CategoriaSimpleDto
import retrofit2.http.*

interface CategoriaApi {

    @GET("api/v1/productos/categorias")
    suspend fun obtenerCategorias(): List<CategoriaSimpleDto>

    @POST("api/v1/productos/categorias")
    suspend fun crearCategoria(
        @Body request: CategoriaRequest
    ): CategoriaSimpleDto

    @PUT("api/v1/productos/categorias/{id}")
    suspend fun actualizarCategoria(
        @Path("id") id: Long,
        @Body request: CategoriaRequest
    ): CategoriaSimpleDto

    @DELETE("api/v1/productos/categorias/{id}")
    suspend fun eliminarCategoria(
        @Path("id") id: Long
    )
}

