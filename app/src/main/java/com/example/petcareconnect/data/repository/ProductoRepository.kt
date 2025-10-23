package com.example.petcareconnect.data.repository

import com.example.petcareconnect.data.db.dao.ProductoDao
import com.example.petcareconnect.data.model.Producto
import kotlinx.coroutines.flow.Flow

class ProductoRepository(private val dao: ProductoDao) {

    // --- 🔹 Insertar o actualizar producto ---
    suspend fun insert(producto: Producto) {
        dao.insert(producto)
    }

    // --- 🔹 Obtener todos los productos ---
    fun getAllProductos(): Flow<List<Producto>> = dao.getAll()

    // --- 🔹 Buscar productos por nombre o categoría ---
    fun searchProductos(query: String): Flow<List<Producto>> = dao.searchProductos(query)

    // --- 🔹 Filtrar productos por categoría ---
    fun getByCategoria(categoriaId: Int): Flow<List<Producto>> = dao.getByCategoria(categoriaId)

    // --- 🔹 Obtener solo productos activos ---
    fun getActivos(): Flow<List<Producto>> = dao.getActivos()

    // --- 🔹 Obtener un producto específico ---
    suspend fun getById(id: Int): Producto? = dao.getById(id)

    // --- 🔹 Eliminar un producto por ID ---
    suspend fun deleteById(id: Int) {
        dao.deleteById(id)
    }
}