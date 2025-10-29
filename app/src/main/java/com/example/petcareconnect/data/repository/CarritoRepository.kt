package com.example.petcareconnect.data.repository

import com.example.petcareconnect.data.db.dao.CarritoDao
import com.example.petcareconnect.data.model.Carrito
import kotlinx.coroutines.flow.Flow

class CarritoRepository(private val dao: CarritoDao) {
    fun getAllItems(): Flow<List<Carrito>> = dao.getAllItems()
    suspend fun insertItem(item: Carrito) = dao.insert(item)
    suspend fun deleteById(id: Int) = dao.deleteById(id)
    suspend fun updateItem(item: Carrito) = dao.update(item)
    suspend fun clearAll() = dao.clearAll()
}
