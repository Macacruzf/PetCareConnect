package com.example.petcareconnect.data.remote.api

import com.example.petcareconnect.data.remote.dto.*
import retrofit2.http.*

interface TicketApi {

    // Crear ticket
    @POST("tickets")
    suspend fun crearTicket(@Body ticket: TicketRequest): TicketResponse

    // Listar todos
    @GET("tickets")
    suspend fun listar(): List<TicketResponse>

    // Obtener por ID
    @GET("tickets/{id}")
    suspend fun obtener(@Path("id") id: Long): TicketResponse

    // Filtrar por estrellas
    @GET("tickets/clasificacion/{estrellas}")
    suspend fun filtrar(@Path("estrellas") estrellas: Int): List<TicketResponse>

    // Filtrar por producto
    @GET("tickets/producto/{idProducto}")
    suspend fun listarPorProducto(@Path("idProducto") idProducto: Long): List<TicketResponse>

    // Agregar comentario
    @POST("tickets/{idTicket}/comentarios")
    suspend fun agregarComentario(
        @Path("idTicket") idTicket: Long,
        @Body comentario: ComentarioRequest
    ): ComentarioResponse

    // Listar comentarios
    @GET("tickets/{idTicket}/comentarios")
    suspend fun obtenerComentarios(
        @Path("idTicket") idTicket: Long
    ): List<ComentarioResponse>
}
