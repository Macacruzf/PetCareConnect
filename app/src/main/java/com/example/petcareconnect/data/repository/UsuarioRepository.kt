package com.example.petcareconnect.data.repository

import com.example.petcareconnect.data.db.dao.UsuarioDao
import com.example.petcareconnect.data.model.Usuario
import com.example.petcareconnect.data.remote.ApiModule
import com.example.petcareconnect.data.remote.dto.*
import com.example.petcareconnect.data.remote.dto.toLocalUsuario
import com.example.petcareconnect.data.session.UserSession
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    private val api = ApiModule.usuarioApi

    // -------------------------------------------------------------------
    // LOGIN (Microservicio Usuario)
    // -------------------------------------------------------------------
    suspend fun login(email: String, pass: String): Usuario? {
        val response = api.login(LoginRequest(email, pass))

        UserSession.token = response.token
        UserSession.rol = response.usuario.rol
        UserSession.usuarioId = response.usuario.idUsuario.toInt()

        val local = response.usuario.toLocalUsuario()
        usuarioDao.insert(local)

        return local
    }

    // -------------------------------------------------------------------
    // REGISTRO (Microservicio Usuario)
    // -------------------------------------------------------------------
    suspend fun register(name: String, email: String, phone: String, pass: String): Usuario? {
        val req = RegisterRequest(
            nombreUsuario = name,
            email = email,
            telefono = phone,
            password = pass
        )

        val response = api.register(req)

        // ✔ guarda token inmediatamente después de registrarse
        UserSession.token = response.token
        UserSession.rol = response.usuario.rol
        UserSession.usuarioId = response.usuario.idUsuario.toInt()

        val local = response.usuario.toLocalUsuario()
        usuarioDao.insert(local)

        return local
    }

    // -------------------------------------------------------------------
    // LISTAR USUARIOS (Remoto + almacenamiento en Room)
    // -------------------------------------------------------------------
    suspend fun syncUsuariosRemotos() {
        val lista = api.listarUsuarios()
        lista.forEach { remote ->
            usuarioDao.insert(remote.toLocalUsuario())
        }
    }

    fun getAllUsuariosLocal(): Flow<List<Usuario>> {
        return usuarioDao.getAllUsuarios()
    }

    // -------------------------------------------------------------------
    // OBTENER USUARIO POR ID (Remoto)
    // -------------------------------------------------------------------
    suspend fun getUsuarioRemoto(id: Long): Usuario? {
        val remote = api.getUsuario(id)
        val local = remote.toLocalUsuario()
        usuarioDao.insert(local)
        return local
    }

    // -------------------------------------------------------------------
    // CRUD LOCAL (Room)
    // -------------------------------------------------------------------
    suspend fun insert(usuario: Usuario) = usuarioDao.insert(usuario)

    suspend fun update(usuario: Usuario) = usuarioDao.update(usuario)

    suspend fun delete(usuario: Usuario) = usuarioDao.delete(usuario)

    suspend fun deleteById(id: Int) = usuarioDao.deleteById(id)

    suspend fun getByEmail(email: String) = usuarioDao.getByEmail(email)
}
