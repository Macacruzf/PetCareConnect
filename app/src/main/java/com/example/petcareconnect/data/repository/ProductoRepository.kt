package com.example.petcareconnect.data.repository

import com.example.petcareconnect.data.db.dao.ProductoDao
import com.example.petcareconnect.data.model.Producto
import kotlinx.coroutines.flow.Flow

class ProductoRepository(private val productoDao: ProductoDao) {

    fun getAllProductos(): Flow<List<Producto>> = productoDao.getAll()

    suspend fun insert(producto: Producto) {
        productoDao.insert(producto)
    }

    suspend fun deleteById(id: Int) {
        productoDao.deleteById(id)
    }
}
