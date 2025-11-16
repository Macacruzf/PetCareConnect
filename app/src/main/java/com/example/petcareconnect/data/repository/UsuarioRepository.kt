package com.example.petcareconnect.data.repository

import com.example.petcareconnect.data.db.dao.UsuarioDao
import com.example.petcareconnect.data.model.Usuario
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    suspend fun insert(usuario: Usuario) {
        usuarioDao.insert(usuario)
    }

    suspend fun login(email: String, password: String): Usuario? {
        return usuarioDao.login(email, password)
    }

    fun getAllUsuarios(): Flow<List<Usuario>> {
        return usuarioDao.getAllUsuarios()
    }

    suspend fun deleteById(id: Int) {
        usuarioDao.deleteById(id)
    }

    suspend fun getById(id: Int): Usuario? {
        return usuarioDao.getById(id)
    }
    suspend fun getByEmail(email: String): Usuario? {
        return usuarioDao.getByEmail(email)
    }

    suspend fun update(usuario: Usuario) {
        usuarioDao.update(usuario)
    }

    suspend fun delete(usuario: Usuario) {
        usuarioDao.delete(usuario)
    }
}