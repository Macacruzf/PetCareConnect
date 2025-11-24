package com.example.petcareconnect.data.remote.api

import com.example.petcareconnect.data.remote.dto.*
import retrofit2.http.*

interface UsuarioApi {

    // LOGIN
    @POST("login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    // REGISTRO
    @POST("register")
    suspend fun register(@Body body: RegisterRequest): Any

    // OBTENER PERFIL
    @GET("perfil/{id}")
    suspend fun getPerfil(@Path("id") id: Int): UsuarioRemote

    // VALIDAR CREDENCIALES (para otros MS)
    @POST("validar-credenciales")
    suspend fun validarCredenciales(@Body body: LoginRequest): ValidacionResponse

    // ROL DEL USUARIO
    @GET("{id}/rol")
    suspend fun getRol(@Path("id") id: Int): Map<String, String>

    // ESTADO DEL USUARIO
    @GET("{id}/estado")
    suspend fun getEstado(@Path("id") id: Int): Map<String, String>

    // LISTAR USUARIOS (requiere pasar idAdmin real)
    @GET("listar/{idAdmin}")
    suspend fun listarUsuarios(@Path("idAdmin") idAdmin: Int): List<UsuarioRemote>
}
