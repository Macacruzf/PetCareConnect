package com.example.petcareconnect.data.db.dao

import androidx.room.*
import com.example.petcareconnect.data.model.Carrito
import kotlinx.coroutines.flow.Flow

@Dao
interface CarritoDao {
    @Query("SELECT * FROM carrito")
    fun getAllItems(): Flow<List<Carrito>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Carrito)

    @Update
    suspend fun update(item: Carrito)

    @Query("DELETE FROM carrito WHERE idItem = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM carrito")
    suspend fun clearAll()
}