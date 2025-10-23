package com.example.petcareconnect.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.petcareconnect.data.model.DetalleVenta
import kotlinx.coroutines.flow.Flow

@Dao
interface DetalleVentaDao {

    // Insertar un nuevo detalle
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(detalle: DetalleVenta)

    //  Listar todos los detalles (opcional)
    @Query("SELECT * FROM detalle_ventas")
    fun getAllDetalles(): Flow<List<DetalleVenta>>

    //  Obtener los detalles por ID de venta (usado en el historial)
    @Query("SELECT * FROM detalle_ventas WHERE ventaId = :ventaId")
    suspend fun getByVentaId(ventaId: Int): List<DetalleVenta>

    // Eliminar todos los detalles (opcional)
    @Query("DELETE FROM detalle_ventas")
    suspend fun deleteAll()
}
