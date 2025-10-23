package com.example.petcareconnect.data.db.dao

import androidx.room.*
import com.example.petcareconnect.data.model.Ticket
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ticket: Ticket)

    @Query("SELECT * FROM tickets ORDER BY idTicket DESC")
    fun getAllTickets(): Flow<List<Ticket>>

    @Query("UPDATE tickets SET estado = :nuevoEstado WHERE idTicket = :id")
    suspend fun updateEstado(id: Int, nuevoEstado: String)

    @Delete
    suspend fun delete(ticket: Ticket)
}