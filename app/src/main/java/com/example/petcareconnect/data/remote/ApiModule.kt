package com.example.petcareconnect.data.remote

import com.example.petcareconnect.data.remote.api.CategoriaApi
import com.example.petcareconnect.data.remote.api.ProductoApi
import com.example.petcareconnect.data.remote.api.TicketApi   // ✅ IMPORT NECESARIO

object ApiModule {

    private const val IP = "10.0.2.2"

    private const val PRODUCTO_URL = "http://$IP:8086/"
    private const val TICKET_URL = "http://$IP:8087/"

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
}
