package com.example.petcareconnect.data.db.dao

import androidx.room.*
import com.example.petcareconnect.data.model.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {

    @Insert
    suspend fun insert(categoria: Categoria)

    // ⭐ LISTADO REACTIVO — PARA OBSERVAR CAMBIOS
    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    fun getAll(): Flow<List<Categoria>>

    // ⭐ LISTADO UNA SOLA VEZ — PARA REPOSITORIOS
    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    suspend fun getAllOnce(): List<Categoria>

    @Query("DELETE FROM categorias WHERE idCategoria = :id")
    suspend fun deleteById(id: Long)

    @Update
    suspend fun update(categoria: Categoria)

    @Query("DELETE FROM categorias")
    suspend fun deleteAll()

    @Query("SELECT * FROM categorias WHERE idCategoria = :id")
    suspend fun getById(id: Long): Categoria?
}




