package com.example.petcareconnect.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.petcareconnect.data.model.Estado
import kotlinx.coroutines.flow.Flow

@Dao
interface EstadoDao {

    // Solo consulta — sin eliminar ni insertar desde la app
    @Query("SELECT * FROM estados ORDER BY idEstado ASC")
    fun getAllEstados(): Flow<List<Estado>>
}