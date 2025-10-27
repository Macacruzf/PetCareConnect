package com.example.petcareconnect.data.repository

import com.example.petcareconnect.data.db.dao.TicketDao
import com.example.petcareconnect.data.model.Ticket
import kotlinx.coroutines.flow.Flow

class TicketRepository(private val dao: TicketDao) {

    fun getTicketsByProducto(idProducto: Int): Flow<List<Ticket>> = dao.getTicketsByProducto(idProducto)

    suspend fun insert(ticket: Ticket) = dao.insert(ticket)

    suspend fun delete(id: Int) = dao.deleteTicket(id)
}
