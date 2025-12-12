package com.example.petcareconnect.data.remote

import com.example.petcareconnect.data.remote.api.CategoriaApi
import com.example.petcareconnect.data.remote.api.ProductoApi
import com.example.petcareconnect.data.remote.api.TicketApi
import com.example.petcareconnect.data.remote.api.UsuarioApi

object ApiModule {

    // ⚙️ CONFIGURACIÓN DE IP
    // - Para EMULADOR: usar "10.0.2.2"
    // - Para DISPOSITIVO FÍSICO: usar tu IP local (ejemplo: "192.168.0.12")
    // - Para HAMACHI/VPN: usar "26.241.40.40"
    private const val IP = "10.0.2.2" // ⚠️ Para EMULADOR (localhost)
    private const val PRODUCTO_URL = "http://$IP:8086/"
    private const val TICKET_URL = "http://$IP:8087/"
    private const val USUARIO_URL = "http://$IP:8081/"

    val productoApi: ProductoApi by lazy {
        RetrofitClient.getClient(PRODUCTO_URL)
            .create(ProductoApi::class.java)
    }

    val categoriaApi: CategoriaApi by lazy {
        RetrofitClient.getClient(PRODUCTO_URL)
            .create(CategoriaApi::class.java)
    }

    val ticketApi: TicketApi by lazy {
        RetrofitClient.getClient(TICKET_URL)
            .create(TicketApi::class.java)
    }

    val usuarioApi: UsuarioApi by lazy {
        RetrofitClient.getClient(USUARIO_URL)
            .create(UsuarioApi::class.java)
    }
}
