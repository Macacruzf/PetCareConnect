package com.example.petcareconnect.data.remote.api

import com.example.petcareconnect.data.remote.dto.TicketRequest
import com.example.petcareconnect.data.remote.dto.TicketResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TicketApi {

    // Crear reseña ⭐
    @POST("api/tickets/crear")
    suspend fun crearTicket(@Body ticket: TicketRequest): TicketResponse

    // Listar todas las reseñas
    @GET("api/tickets")
    suspend fun listar(): List<TicketResponse>

    // Obtener un ticket
    @GET("api/tickets/{id}")
    suspend fun obtener(@Path("id") id: Long): TicketResponse

    // Filtrar por cantidad de estrellas
    @GET("api/tickets/clasificacion/{estrellas}")
    suspend fun filtrar(@Path("estrellas") estrellas: Int): List<TicketResponse>

    // ⭐ **LISTAR RESEÑAS POR PRODUCTO**
    @GET("api/tickets/producto/{idProducto}")
    suspend fun listarPorProducto(@Path("idProducto") idProducto: Long): List<TicketResponse>
}
