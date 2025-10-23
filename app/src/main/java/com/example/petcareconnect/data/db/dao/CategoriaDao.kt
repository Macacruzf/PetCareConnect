package com.example.petcareconnect.data.db.dao

import androidx.room.*
import com.example.petcareconnect.data.model.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoria: Categoria)

    @Query("SELECT * FROM categorias ORDER BY idCategoria DESC")
    fun getAll(): Flow<List<Categoria>>

    @Query("DELETE FROM categorias WHERE idCategoria = :id")
    suspend fun deleteById(id: Int)

    @Update
    suspend fun update(categoria: Categoria)
}