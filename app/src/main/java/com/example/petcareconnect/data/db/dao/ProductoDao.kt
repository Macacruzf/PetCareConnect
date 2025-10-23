package com.example.petcareconnect.data.db.dao

import androidx.room.*
import com.example.petcareconnect.data.model.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    // --- 🔹 Insertar o actualizar producto ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(producto: Producto)

    // --- 🔹 Obtener todos los productos (ordenados) ---
    @Query("SELECT * FROM productos ORDER BY idProducto DESC")
    fun getAll(): Flow<List<Producto>>

    // --- 🔹 Buscar productos por nombre o categoría ---
    @Query("""
        SELECT * FROM productos 
        WHERE nombre LIKE '%' || :query || '%' 
        OR categoriaId IN (
            SELECT idCategoria FROM categorias WHERE nombre LIKE '%' || :query || '%'
        )
        ORDER BY idProducto DESC
    """)
    fun searchProductos(query: String): Flow<List<Producto>>

    // --- 🔹 Filtrar por categoría específica ---
    @Query("""
        SELECT * FROM productos 
        WHERE categoriaId = :categoriaId
        ORDER BY idProducto DESC
    """)
    fun getByCategoria(categoriaId: Int): Flow<List<Producto>>

    // --- 🔹 Filtrar productos activos ---
    @Query("""
        SELECT * FROM productos 
        WHERE estadoId IN (
            SELECT idEstado FROM estados WHERE nombre = 'Activo'
        )
        ORDER BY idProducto DESC
    """)
    fun getActivos(): Flow<List<Producto>>

    // --- 🔹 Obtener producto por ID ---
    @Query("SELECT * FROM productos WHERE idProducto = :id LIMIT 1")
    suspend fun getById(id: Int): Producto?

    // --- 🔹 Eliminar producto por ID ---
    @Query("DELETE FROM productos WHERE idProducto = :id")
    suspend fun deleteById(id: Int)
}
