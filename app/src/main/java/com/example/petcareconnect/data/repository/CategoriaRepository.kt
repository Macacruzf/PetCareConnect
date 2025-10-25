package com.example.petcareconnect.data.repository

import com.example.petcareconnect.data.db.dao.CategoriaDao
import com.example.petcareconnect.data.model.Categoria
import kotlinx.coroutines.flow.Flow

class CategoriaRepository(private val dao: CategoriaDao) {

    suspend fun insert(categoria: Categoria) = dao.insert(categoria)

    fun getAllCategorias(): Flow<List<Categoria>> = dao.getAll() // ✅ ahora coincide con el DAO

    suspend fun deleteById(id: Int) = dao.deleteById(id)

    suspend fun update(categoria: Categoria) = dao.update(categoria)

    suspend fun getAllOnce(): List<Categoria> = dao.getAllOnce() // ✅ para usarse en la semilla inicial
}
