package com.example.petcareconnect.ui.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.petcareconnect.navigation.AppNavGraph
import com.example.petcareconnect.ui.viewmodel.AuthViewModel
import com.example.petcareconnect.ui.viewmodel.ProductoViewModel
import com.example.petcareconnect.ui.viewmodel.TicketViewModel

/*
 * Componente raíz de la aplicación.
 * Se encarga de iniciar la navegación y pasar los ViewModels globales.
 */
@Composable
fun AppRootScreen(
    authViewModel: AuthViewModel,       // Login y sesión
    productoViewModel: ProductoViewModel, // Productos, categorías, stock
    ticketViewModel: TicketViewModel      // ⭐ Comentarios y reseñas (nuevo)
) {

    val navController = rememberNavController()

    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {

            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                productoViewModel = productoViewModel,
                ticketViewModel = ticketViewModel   // ← ⭐ Lo enviamos al grafo
            )
        }
    }
}
