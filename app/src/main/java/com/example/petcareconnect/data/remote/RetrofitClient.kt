package com.example.petcareconnect.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Se inyectará la función que entrega el token almacenado
    private var tokenProvider: (() -> String?)? = null

    fun initTokenProvider(provider: () -> String?) {
        tokenProvider = provider
    }

    fun getClient(baseUrl: String): Retrofit {

        val client = OkHttpClient.Builder()
            .addInterceptor(
                AuthInterceptor { tokenProvider?.invoke() }
            )
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)               // 🔥 Ahora sí enviamos headers!!
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
