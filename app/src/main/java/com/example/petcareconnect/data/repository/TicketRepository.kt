package com.example.petcareconnect.data.repository

import com.example.petcareconnect.data.db.dao.TicketDao
import com.example.petcareconnect.data.model.Ticket
import kotlinx.coroutines.flow.Flow

class TicketRepository(private val dao: TicketDao) {

    fun getAllTickets(): Flow<List<Ticket>> = dao.getAllTickets()

    suspend fun insert(ticket: Ticket) = dao.insert(ticket)

    suspend fun updateEstado(id: Int, estado: String) = dao.updateEstado(id, estado)

    suspend fun delete(ticket: Ticket) = dao.delete(ticket)
}
