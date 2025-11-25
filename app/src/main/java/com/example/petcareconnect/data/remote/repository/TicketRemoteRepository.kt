package com.example.petcareconnect.data.remote.repository

import com.example.petcareconnect.data.remote.ApiModule
import com.example.petcareconnect.data.remote.dto.*

class TicketRemoteRepository {

    private val api = ApiModule.ticketApi

    // ========================================
    //              TICKETS
    // ========================================

    // Crear ticket ⭐
    suspend fun crearTicket(req: TicketRequest): TicketResponse =
        api.crearTicket(req)

    // Listar todo
    suspend fun listar(): List<TicketResponse> =
        api.listar()

    // Obtener por ID
    suspend fun obtener(id: Long): TicketResponse =
        api.obtener(id)

    // Filtrar por clasificación ⭐
    suspend fun filtrar(estrellas: Int): List<TicketResponse> =
        api.filtrar(estrellas)

    // Listar por producto
    suspend fun listarPorProducto(idProducto: Long): List<TicketResponse> =
        api.listarPorProducto(idProducto)


    // ========================================
    //             COMENTARIOS
    // ========================================

    // Agregar comentario ⭐
    suspend fun agregarComentario(
        idTicket: Long,
        comentario: ComentarioRequest
    ): ComentarioResponse =
        api.agregarComentario(idTicket, comentario)

    // Obtener comentarios de un ticket
    suspend fun obtenerComentarios(idTicket: Long): List<ComentarioResponse> =
        api.obtenerComentarios(idTicket)
}
