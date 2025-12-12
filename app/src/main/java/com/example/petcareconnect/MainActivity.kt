package com.example.petcareconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.petcareconnect.data.db.PetCareDatabase

// Repos locales
import com.example.petcareconnect.data.repository.UsuarioRepository
import com.example.petcareconnect.data.repository.ProductoRepository
import com.example.petcareconnect.data.repository.CategoriaRepository

// ViewModels
import com.example.petcareconnect.ui.viewmodel.AuthViewModel
import com.example.petcareconnect.ui.viewmodel.AuthViewModelFactory
import com.example.petcareconnect.ui.viewmodel.ProductoViewModel
import com.example.petcareconnect.ui.viewmodel.ProductoViewModelFactory
import com.example.petcareconnect.ui.viewmodel.TicketViewModel
import com.example.petcareconnect.ui.viewmodel.TicketViewModelFactory

// Pantalla raíz
import com.example.petcareconnect.ui.screen.AppRootScreen
// Notificaciones
import com.example.petcareconnect.util.NotificationHelper

// Remote repositories y API
import com.example.petcareconnect.data.remote.ProductoRemoteRepository
import com.example.petcareconnect.data.remote.repository.TicketRemoteRepository
import com.example.petcareconnect.data.remote.ApiModule

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ⭐ Inicializar canal de notificaciones
        NotificationHelper.createNotificationChannel(this)

        // ⭐ Repos remotos (únicos que usaremos ahora)
        val productoRemoteRepo = ProductoRemoteRepository(ApiModule.productoApi)
        val ticketRemoteRepo = TicketRemoteRepository()

        // ⭐ ViewModel Auth
        val authViewModel = AuthViewModelFactory()
            .create(AuthViewModel::class.java)

        // ⭐ ViewModel Productos (solo remoto)
        val productoViewModel = ProductoViewModelFactory(
            productoRemoteRepo
        ).create(ProductoViewModel::class.java)

        // ⭐ ViewModel Tickets
        val ticketViewModel = TicketViewModelFactory(ticketRemoteRepo)
            .create(TicketViewModel::class.java)

        // ⭐ Render App
        setContent {
            AppRootScreen(
                authViewModel = authViewModel,
                productoViewModel = productoViewModel,
                ticketViewModel = ticketViewModel
            )
        }
    }
}