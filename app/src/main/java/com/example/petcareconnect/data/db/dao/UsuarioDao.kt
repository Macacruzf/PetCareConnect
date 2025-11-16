package com.example.petcareconnect.data.db.dao

import androidx.room.*
import com.example.petcareconnect.data.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    // INSERTAR/CREAR USUARIO
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usuario: Usuario)

    // LOGIN (ignorando may/min y espacios)
    @Query("""
        SELECT * FROM usuario
        WHERE LOWER(TRIM(email)) = LOWER(TRIM(:email))
        AND password = :password
        LIMIT 1
    """)
    suspend fun login(email: String, password: String): Usuario?

    // LISTAR TODOS LOS USUARIOS
    @Query("SELECT * FROM usuario ORDER BY idUsuario DESC")
    fun getAllUsuarios(): Flow<List<Usuario>>

    // OBTENER POR ID
    @Query("SELECT * FROM usuario WHERE idUsuario = :id LIMIT 1")
    suspend fun getById(id: Int): Usuario?

    // OBTENER POR EMAIL (único)
    @Query("""
        SELECT * FROM usuario 
        WHERE LOWER(TRIM(email)) = LOWER(TRIM(:email))
        LIMIT 1
    """)
    suspend fun getByEmail(email: String): Usuario?

    // ACTUALIZAR USUARIO
    @Update
    suspend fun update(usuario: Usuario)

    // BORRAR UN USUARIO COMPLETO
    @Delete
    suspend fun delete(usuario: Usuario)

    // BORRAR POR ID
    @Query("DELETE FROM usuario WHERE idUsuario = :id")
    suspend fun deleteById(id: Int)
}
