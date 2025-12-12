package com.example.petcareconnect.data.remote.api

import com.example.petcareconnect.data.remote.dto.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface UsuarioApi {

    // --------------------------------------------------------
    // 🔐 LOGIN
    // --------------------------------------------------------
    @POST("api/usuarios/login")
    suspend fun login(
        @Body body: LoginRequest
    ): LoginResponse

    // --------------------------------------------------------
    // 🧾 REGISTRO
    // --------------------------------------------------------
    @POST("api/usuarios/register")
    suspend fun register(
        @Body body: RegisterRequest
    ): UsuarioRemote

    // --------------------------------------------------------
    // 👤 PERFIL
    // --------------------------------------------------------
    @GET("api/usuarios/perfil/{id}")
    suspend fun getPerfil(
        @Path("id") id: Int
    ): UsuarioRemote

    @PUT("api/usuarios/perfil/{id}")
    suspend fun updatePerfil(
        @Path("id") id: Int,
        @Body body: UsuarioRemote
    ): UsuarioRemote

    // --------------------------------------------------------
    // 🔑 CAMBIAR CONTRASEÑA
    // --------------------------------------------------------
    @POST("api/usuarios/cambiar-password/{id}")
    suspend fun changePassword(
        @Path("id") id: Int,
        @Body body: ChangePasswordRequest
    ): ChangePasswordResponse

    // --------------------------------------------------------
    // 🔒 VALIDAR CREDENCIALES
    // --------------------------------------------------------
    @POST("api/usuarios/validar-credenciales")
    suspend fun validarCredenciales(
        @Body body: LoginRequest
    ): ValidacionResponse

    // --------------------------------------------------------
    // 🛡️ ROLES Y ESTADO
    // --------------------------------------------------------
    @GET("api/usuarios/{id}/rol")
    suspend fun getRol(
        @Path("id") id: Int
    ): Map<String, String>

    @GET("api/usuarios/{id}/estado")
    suspend fun getEstado(
        @Path("id") id: Int
    ): Map<String, String>

    // --------------------------------------------------------
    // 📋 LISTAR USUARIOS (solo admin)
    // --------------------------------------------------------
    @GET("api/usuarios/listar/{idAdmin}")
    suspend fun listarUsuarios(
        @Path("idAdmin") idAdmin: Int
    ): List<UsuarioRemote>

    // --------------------------------------------------------
    // ❌ ELIMINAR USUARIO
    // --------------------------------------------------------
    @DELETE("api/usuarios/{id}")
    suspend fun deleteUser(
        @Path("id") id: Int
    )

    // --------------------------------------------------------
    // 📸 FOTO DE PERFIL
    // --------------------------------------------------------
    @Multipart
    @POST("api/usuarios/{id}/foto")
    suspend fun subirFotoPerfil(
        @Path("id") id: Int,
        @Part foto: MultipartBody.Part
    ): Map<String, Any>

    @GET("api/usuarios/{id}/foto")
    suspend fun obtenerFotoPerfil(
        @Path("id") id: Int
    ): ResponseBody

    @DELETE("api/usuarios/{id}/foto")
    suspend fun eliminarFotoPerfil(
        @Path("id") id: Int
    ): Map<String, Any>
}
