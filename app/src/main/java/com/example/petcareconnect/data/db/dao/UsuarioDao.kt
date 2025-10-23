package com.example.petcareconnect.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.petcareconnect.data.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usuario: Usuario)

    @Query("SELECT * FROM usuario WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): Usuario?

    @Query("SELECT * FROM usuario ORDER BY id DESC")
    fun getAllUsuarios(): Flow<List<Usuario>>

    @Query("SELECT * FROM usuario WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Usuario?

    @Query("SELECT * FROM usuario WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): Usuario?

    @Query("DELETE FROM usuario WHERE id = :id")
    suspend fun deleteById(id: Int)
}