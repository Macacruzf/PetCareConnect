package com.example.petcareconnect.data.remote.repository

import com.example.petcareconnect.data.remote.ApiModule
import com.example.petcareconnect.data.remote.dto.TicketRequest
import com.example.petcareconnect.data.remote.dto.TicketResponse

class TicketRemoteRepository {

    private val api = ApiModule.ticketApi

    // Crear reseña ⭐
    suspend fun crearTicket(req: TicketRequest): TicketResponse =
        api.crearTicket(req)

    // Listar todo
    suspend fun listar(): List<TicketResponse> =
        api.listar()

    // Obtener por ID
    suspend fun obtener(id: Long): TicketResponse =
        api.obtener(id)

    // Filtrar por estrellas ⭐
    suspend fun filtrar(estrellas: Int): List<TicketResponse> =
        api.filtrar(estrellas)

    // Listar por producto
    suspend fun listarPorProducto(idProducto: Long): List<TicketResponse> =
        api.listarPorProducto(idProducto)
}
