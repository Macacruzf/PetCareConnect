package com.example.petcareconnect.data.db.dao

import androidx.room.*
import com.example.petcareconnect.data.model.Ticket
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ticket: Ticket)

    @Query("SELECT * FROM tickets WHERE idProducto = :idProducto ORDER BY fecha DESC")
    fun getTicketsByProducto(idProducto: Int): Flow<List<Ticket>>

    @Query("DELETE FROM tickets WHERE idTicket = :idTicket")
    suspend fun deleteTicket(idTicket: Int)
}
