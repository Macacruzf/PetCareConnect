package com.example.petcareconnect.data.db.dao

import androidx.room.*
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.model.EstadoProducto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    // Obtener todos (FLOW para Compose)
    @Query("SELECT * FROM productos ORDER BY idProducto DESC")
    fun getAll(): Flow<List<Producto>>

    // Obtener todos una vez
    @Query("SELECT * FROM productos ORDER BY idProducto DESC")
    suspend fun getAllOnce(): List<Producto>

    // Insertar nuevo
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(producto: Producto)

    // Eliminar por ID
    @Query("DELETE FROM productos WHERE idProducto = :id")
    suspend fun deleteById(id: Long)

    // Actualizar producto completo
    @Update
    suspend fun update(producto: Producto)

    // Actualizar stock solamente
    @Query("UPDATE productos SET stock = :nuevoStock WHERE idProducto = :id")
    suspend fun updateStock(id: Int, nuevoStock: Int)

    // Actualizar estado solamente
    @Query("UPDATE productos SET estado = :nuevoEstado WHERE idProducto = :id")
    suspend fun updateEstado(id: Int, nuevoEstado: EstadoProducto)

    // 🔹 NUEVO: actualizar SOLO la imagen desde URI
    @Query("UPDATE productos SET imagenUri = :nuevaUri WHERE idProducto = :id")
    suspend fun updateImagenUri(id: Int, nuevaUri: String?)
    @Query("DELETE FROM productos")
    suspend fun deleteAll()

}
