package com.example.petcareconnect.data.db.dao

import androidx.room.*
import com.example.petcareconnect.data.model.Venta
import kotlinx.coroutines.flow.Flow

@Dao
interface VentaDao {

    @Query("SELECT * FROM ventas ORDER BY fecha DESC")
    fun getAllVentas(): Flow<List<Venta>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(venta: Venta): Long

    @Query("DELETE FROM ventas WHERE idVenta = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM ventas WHERE idVenta = :id")
    suspend fun getById(id: Int): Venta?
}