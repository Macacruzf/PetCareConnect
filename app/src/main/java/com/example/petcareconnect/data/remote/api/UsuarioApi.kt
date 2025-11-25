package com.example.petcareconnect.data.remote.api

import com.example.petcareconnect.data.remote.dto.*
import retrofit2.http.*

interface UsuarioApi {

    // --------------------------------------------------------
    // 🔐 LOGIN
    // --------------------------------------------------------
    @POST("usuario/login")
    suspend fun login(
        @Body body: LoginRequest
    ): LoginResponse

    // --------------------------------------------------------
    // 🧾 REGISTRO
    // --------------------------------------------------------
    @POST("usuario/register")
    suspend fun register(
        @Body body: RegisterRequest
    ): UsuarioRemote

    // --------------------------------------------------------
    // 👤 PERFIL
    // --------------------------------------------------------
    @GET("usuario/perfil/{id}")
    suspend fun getPerfil(
        @Path("id") id: Int
    ): UsuarioRemote

    @PUT("usuario/perfil/{id}")
    suspend fun updatePerfil(
        @Path("id") id: Int,
        @Body body: UsuarioRemote
    ): UsuarioRemote

    // --------------------------------------------------------
    // 🔒 VALIDAR CREDENCIALES
    // --------------------------------------------------------
    @POST("usuario/validar-credenciales")
    suspend fun validarCredenciales(
        @Body body: LoginRequest
    ): ValidacionResponse

    // --------------------------------------------------------
    // 🛡️ ROLES Y ESTADO
    // --------------------------------------------------------
    @GET("usuario/{id}/rol")
    suspend fun getRol(
        @Path("id") id: Int
    ): Map<String, String>

    @GET("usuario/{id}/estado")
    suspend fun getEstado(
        @Path("id") id: Int
    ): Map<String, String>

    // --------------------------------------------------------
    // 📋 LISTAR USUARIOS (solo admin)
    // --------------------------------------------------------
    @GET("usuario/listar/{idAdmin}")
    suspend fun listarUsuarios(
        @Path("idAdmin") idAdmin: Int
    ): List<UsuarioRemote>

    // --------------------------------------------------------
    // ❌ ELIMINAR USUARIO
    // --------------------------------------------------------
    @DELETE("usuario/{id}")
    suspend fun deleteUser(
        @Path("id") id: Int
    )
}
