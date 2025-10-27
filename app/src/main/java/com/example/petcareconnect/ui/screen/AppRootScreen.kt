package com.example.petcareconnect.ui.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.petcareconnect.navigation.AppNavGraph
import com.example.petcareconnect.ui.viewmodel.AuthViewModel
import com.example.petcareconnect.ui.viewmodel.ProductoViewModel

/*
 *  AppRootScreen conecta los ViewModels principales con la navegación general.
 */
@Composable
fun AppRootScreen(
    authViewModel: AuthViewModel,
    productoViewModel: ProductoViewModel
) {
    val navController = rememberNavController()

    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            // Pasamos los ViewModels a la navegación
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                productoViewModel = productoViewModel
            )
        }
    }
}
