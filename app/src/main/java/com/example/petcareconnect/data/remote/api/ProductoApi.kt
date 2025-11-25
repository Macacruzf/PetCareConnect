package com.example.petcareconnect.data.remote.api

import com.example.petcareconnect.data.remote.dto.EstadoRequest
import com.example.petcareconnect.data.remote.dto.ProductoDto
import com.example.petcareconnect.data.remote.dto.ProductoUpdateRequest
import retrofit2.http.*

interface ProductoApi {

    @GET("api/v1/productos/movil")
    suspend fun obtenerProductos(): List<ProductoDto>

    @POST("api/v1/productos")
    suspend fun crearProducto(
        @Body producto: ProductoDto
    ): ProductoDto

    @PUT("api/v1/productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Long,
        @Body request: ProductoUpdateRequest
    ): ProductoDto

    @PUT("api/v1/productos/{id}/estado")
    suspend fun cambiarEstadoProducto(
        @Path("id") id: Long,
        @Body estado: EstadoRequest
    ): Map<String, Any>

    @DELETE("api/v1/productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Long)
}
