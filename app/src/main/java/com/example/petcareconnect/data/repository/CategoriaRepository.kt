package com.example.petcareconnect.data.repository

import com.example.petcareconnect.data.db.dao.CategoriaDao
import com.example.petcareconnect.data.model.Categoria
import kotlinx.coroutines.flow.Flow

class CategoriaRepository(private val dao: CategoriaDao) {

    suspend fun insert(categoria: Categoria) = dao.insert(categoria)

    fun getAllCategorias(): Flow<List<Categoria>> = dao.getAll()

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun update(categoria: Categoria) = dao.update(categoria)

    suspend fun getAllOnce(): List<Categoria> = dao.getAllOnce()

    suspend fun deleteAll() = dao.deleteAll()

    // Obtener por ID solo devuelve Categoria local
    suspend fun getById(idCategoria: Long): Categoria? {
        return dao.getById(idCategoria)
    }
}
