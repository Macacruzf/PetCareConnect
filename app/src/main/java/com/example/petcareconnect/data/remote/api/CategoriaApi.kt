package com.example.petcareconnect.data.remote.api

import com.example.petcareconnect.data.remote.dto.CategoriaDto
import retrofit2.http.*

interface CategoriaApi {

    // =============================================
    // LISTAR CATEGORÍAS — RUTA CORRECTA
    // =============================================
    @GET("api/v1/productos/categorias")
    suspend fun obtenerCategorias(): List<CategoriaDto>

    // =============================================
    // CREAR CATEGORÍA
    // =============================================
    @POST("api/v1/productos/categorias")
    suspend fun crearCategoria(
        @Body body: Map<String, Any>
    ): CategoriaDto

    // =============================================
    // ACTUALIZAR CATEGORÍA
    // =============================================
    @PUT("api/v1/productos/categorias/{id}")
    suspend fun actualizarCategoria(
        @Path("id") id: Long,
        @Body body: Map<String, Any>
    ): CategoriaDto

    // =============================================
    // ELIMINAR CATEGORÍA
    // =============================================
    @DELETE("api/v1/productos/categorias/{id}")
    suspend fun eliminarCategoria(
        @Path("id") id: Long
    )
}
