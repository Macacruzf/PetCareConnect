package com.example.petcareconnect.data.repository

import com.example.petcareconnect.data.db.dao.ProductoDao
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.model.EstadoProducto
import kotlinx.coroutines.flow.Flow

class ProductoRepository(private val productoDao: ProductoDao) {

    // Obtener todos los productos como Flow (para Compose)
    fun getAllProductos(): Flow<List<Producto>> = productoDao.getAll()

    // Obtener todos los productos una sola vez (para seeds u operaciones puntuales)
    suspend fun getAllOnce(): List<Producto> = productoDao.getAllOnce()

    // Insertar producto
    suspend fun insert(producto: Producto) {
        productoDao.insert(producto)
    }

    // Eliminar producto por ID
    suspend fun deleteById(id: Int) {
        productoDao.deleteById(id)
    }

    // 🔹 ACTUALIZAR SOLO EL STOCK
    suspend fun updateStock(idProducto: Int, nuevoStock: Int) {
        productoDao.updateStock(idProducto, nuevoStock)
    }

    // 🔹 ACTUALIZAR SOLO EL ESTADO (DISPONIBLE, NO_DISPONIBLE, SIN_STOCK)
    suspend fun updateEstado(idProducto: Int, nuevoEstado: EstadoProducto) {
        productoDao.updateEstado(idProducto, nuevoEstado)
    }

    // 🔹 ACTUALIZAR PRODUCTO COMPLETO
    suspend fun update(producto: Producto) {
        productoDao.update(producto)
    }
}
