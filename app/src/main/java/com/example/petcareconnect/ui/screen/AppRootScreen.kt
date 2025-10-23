package com.example.petcareconnect.ui.screen


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.petcareconnect.navigation.AppNavGraph

/*
 * AppRoot() es el punto raíz de toda la interfaz gráfica.
 * Aquí se define el tema visual y se configura la navegación general.
 */
@Composable
fun AppRootScreen() {
    // Crea un controlador de navegación que recuerda la pantalla actual.
    val navController = rememberNavController()

    // MaterialTheme aplica colores, tipografía y estilo general (por ahora el tema por defecto).
    MaterialTheme {
        // Surface es un contenedor visual. Sirve para aplicar el fondo general.
        Surface(color = MaterialTheme.colorScheme.background) {
            // Carga el gráfico de navegación principal que contiene todas las pantallas.
            AppNavGraph(navController = navController)
        }
    }
}