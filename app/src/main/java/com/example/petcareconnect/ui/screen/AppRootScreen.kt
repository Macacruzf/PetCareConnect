package com.example.petcareconnect.ui.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.petcareconnect.navigation.AppNavGraph
import com.example.petcareconnect.ui.viewmodel.AuthViewModel
import com.example.petcareconnect.ui.viewmodel.ProductoViewModel

/*
 * Componente raíz de la aplicación.
 * Su función principal es inicializar el controlador de navegación (NavController)
 * y conectar los ViewModels globales con el sistema de rutas definido en AppNavGraph.
 */
@Composable
fun AppRootScreen(
    authViewModel: AuthViewModel,       // ViewModel encargado de la autenticación del usuario
    productoViewModel: ProductoViewModel // ViewModel que gestiona los productos y categorías
) {
    // Se crea un controlador de navegación para gestionar las rutas dentro de la app.
    // rememberNavController() garantiza que el controlador conserve su estado
    // a lo largo de recomposiciones (por ejemplo, al rotar la pantalla o actualizar datos).
    val navController = rememberNavController()

    // MaterialTheme aplica los estilos y colores globales definidos por Material 3.
    // Controla aspectos visuales como tipografía, paleta de colores y componentes base.
    MaterialTheme {
        // Surface actúa como contenedor principal con un color de fondo uniforme.
        // Este fondo se obtiene del esquema de colores del tema activo.
        Surface(color = MaterialTheme.colorScheme.background) {

            // Se invoca el grafo de navegación principal de la app.
            // Aquí se conectan los ViewModels principales y el controlador de navegación.
            // De esta forma, AppNavGraph tiene acceso a la lógica de autenticación y productos
            // para renderizar las pantallas correspondientes.
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                productoViewModel = productoViewModel
            )
        }
    }
}
