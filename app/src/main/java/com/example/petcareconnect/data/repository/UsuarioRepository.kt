package com.example.petcareconnect.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.petcareconnect.data.db.dao.UsuarioDao
import com.example.petcareconnect.data.model.Usuario
import com.example.petcareconnect.data.remote.ApiModule
import com.example.petcareconnect.data.remote.dto.*
import com.example.petcareconnect.data.remote.dto.toLocalUsuario
import com.example.petcareconnect.data.session.UserSession
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

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

    // -------------------------------------------------------------------
    // FOTO DE PERFIL
    // -------------------------------------------------------------------

    /**
     * Sube una foto de perfil al servidor
     * @param idUsuario ID del usuario
     * @param bitmap Imagen en formato Bitmap
     * @return true si se subió correctamente
     */
    suspend fun subirFotoPerfil(idUsuario: Int, bitmap: Bitmap): Boolean {
        return try {
            // Comprimir bitmap a JPEG
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
            val byteArray = stream.toByteArray()

            // Crear MultipartBody.Part
            val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
            val fotoPart = MultipartBody.Part.createFormData("foto", "foto_perfil.jpg", requestBody)

            // Subir al servidor
            val response = api.subirFotoPerfil(idUsuario, fotoPart)

            // Verificar respuesta
            response["success"] == true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Obtiene la foto de perfil del servidor
     * @param idUsuario ID del usuario
     * @return Bitmap de la foto o null si no existe
     */
    suspend fun obtenerFotoPerfil(idUsuario: Int): Bitmap? {
        return try {
            val responseBody = api.obtenerFotoPerfil(idUsuario)
            val bytes = responseBody.bytes()
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Elimina la foto de perfil del servidor
     * @param idUsuario ID del usuario
     * @return true si se eliminó correctamente
     */
    suspend fun eliminarFotoPerfil(idUsuario: Int): Boolean {
        return try {
            val response = api.eliminarFotoPerfil(idUsuario)
            response["success"] == true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
