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
    // LOGIN (Microservicio Usuario) — sin token
    // -------------------------------------------------------------------
    suspend fun login(email: String, pass: String): Usuario? {

        val response = api.login(LoginRequest(email, pass))

        val remoteUsuario = response.usuario ?: return null

        // Guardar en sesión
        UserSession.usuarioId = remoteUsuario.idUsuario
        UserSession.rol = remoteUsuario.rol
        UserSession.estado = remoteUsuario.estado

        val local = remoteUsuario.toLocalUsuario()
        usuarioDao.insert(local)

        return local
    }

    // -------------------------------------------------------------------
    // REGISTRO (Microservicio Usuario) — sin token
    // -------------------------------------------------------------------
    suspend fun register(
        name: String,
        email: String,
        phone: String,
        pass: String
    ): Usuario? {

        val req = RegisterRequest(
            nombreUsuario = name,
            email = email,
            telefono = phone,
            password = pass,
            rol = "CLIENTE"
        )

        val remoteUser = api.register(req)

        // Guardar en sesión
        UserSession.usuarioId = remoteUser.idUsuario
        UserSession.rol = remoteUser.rol
        UserSession.estado = remoteUser.estado

        val local = remoteUser.toLocalUsuario()
        usuarioDao.insert(local)

        return local
    }

    // -------------------------------------------------------------------
    // LISTAR USUARIOS (ADMIN)
    // -------------------------------------------------------------------
    suspend fun syncUsuariosRemotos(adminId: Int) {
        val lista = api.listarUsuarios(adminId)
        lista.forEach { remote ->
            usuarioDao.insert(remote.toLocalUsuario())
        }
    }

    fun getAllUsuariosLocal(): Flow<List<Usuario>> {
        return usuarioDao.getAllUsuarios()
    }

    // -------------------------------------------------------------------
    // ACTUALIZAR USUARIO REMOTO (ADMIN)
    // -------------------------------------------------------------------
    suspend fun updateUsuarioRemoto(usuario: Usuario): Usuario? {

        val body = UsuarioRemote(
            idUsuario = usuario.idUsuario,
            nombreUsuario = usuario.nombreUsuario,
            email = usuario.email,
            telefono = usuario.telefono,
            password = usuario.password,
            rol = usuario.rol,
            estado = usuario.estado
        )

        val actualizado = api.updatePerfil(usuario.idUsuario, body)

        val local = actualizado.toLocalUsuario()
        usuarioDao.update(local)

        return local
    }

    // -------------------------------------------------------------------
    // CRUD LOCAL
    // -------------------------------------------------------------------
    suspend fun insert(usuario: Usuario) = usuarioDao.insert(usuario)
    suspend fun update(usuario: Usuario) = usuarioDao.update(usuario)
    suspend fun delete(usuario: Usuario) = usuarioDao.delete(usuario)
    suspend fun deleteById(id: Int) = usuarioDao.deleteById(id)
    suspend fun getByEmail(email: String) = usuarioDao.getByEmail(email)
}
