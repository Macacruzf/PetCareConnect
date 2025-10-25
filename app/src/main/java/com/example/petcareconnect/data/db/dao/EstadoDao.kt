package com.example.petcareconnect.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.petcareconnect.data.model.Estado
import kotlinx.coroutines.flow.Flow

@Dao
interface EstadoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(estado: Estado)

    // 🔹 Flujo reactivo (para observar cambios)
    @Query("SELECT * FROM estados ORDER BY idEstado ASC")
    fun getAllEstados(): Flow<List<Estado>>

    // 🔹 Consulta directa (para inicializar datos)
    @Query("SELECT * FROM estados ORDER BY idEstado ASC")
    suspend fun getAllOnce(): List<Estado>

    @Query("DELETE FROM estados")
    suspend fun deleteAll()
}

