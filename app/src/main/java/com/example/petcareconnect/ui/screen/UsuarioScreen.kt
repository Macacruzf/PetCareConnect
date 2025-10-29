package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/*
 * ---------------------------------------------------------------------------
 * UsuarioScreen
 * ---------------------------------------------------------------------------
 * Pantalla que muestra una lista de usuarios registrados en el sistema.
 * En esta versión inicial:
 *  - Es un panel de solo lectura (no editable).
 *  - Utiliza datos simulados (no provienen aún de una base de datos real).
 *  - Emplea componentes Material 3 con animaciones sutiles de elevación,
 *    sombras y transiciones visuales al interactuar.
 * ---------------------------------------------------------------------------
 */
@Composable
fun UsuarioScreen() {

    // -----------------------------------------------------------------------
    // Lista de usuarios de ejemplo (mock data)
    // -----------------------------------------------------------------------
    // Estos datos sirven para representar la estructura visual del listado.
    // En una versión futura, se reemplazará por datos obtenidos desde
    // una API o base de datos local (Room / Firebase).
    // -----------------------------------------------------------------------
    val usuarios = listOf(
        "Verónica Rojas (Admin)",
        "Carlos Díaz (Veterinario)",
        "Ana Pérez (Recepcionista)",
        "Pedro Salazar (Cliente)"
    )

    // -----------------------------------------------------------------------
    // CONTENEDOR PRINCIPAL
    // -----------------------------------------------------------------------
    // Box organiza el contenido centrado y define el fondo general.
    // Incluye color de fondo gris claro (#F5F5F5) para resaltar las tarjetas.
    // -----------------------------------------------------------------------
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {

        // -------------------------------------------------------------------
        // COLUMNA PRINCIPAL
        // -------------------------------------------------------------------
        // Alinea verticalmente los elementos, centrando el encabezado y las
        // tarjetas con separación uniforme entre ellas.
        // -------------------------------------------------------------------
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            // ---------------------------------------------------------------
            // ENCABEZADO DE SECCIÓN
            // ---------------------------------------------------------------
            // Muestra el título de la pantalla con tipografía grande y negrita.
            // Material 3 aplica una animación de opacidad sutil en recomposición.
            // ---------------------------------------------------------------
            Text(
                text = "Usuarios registrados",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            // ---------------------------------------------------------------
            // LISTA DE TARJETAS DE USUARIO
            // ---------------------------------------------------------------
            // Cada usuario se representa con una tarjeta (Card) individual.
            // La sombra y elevación aplicadas producen un efecto visual suave
            // al mostrarse o recomponerse.
            // ---------------------------------------------------------------
            usuarios.forEach { nombre ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {

                    // -------------------------------------------------------
                    // FILA INTERNA DE CADA TARJETA
                    // -------------------------------------------------------
                    // Cada fila contiene el nombre del usuario centrado
                    // verticalmente dentro de un padding.
                    // -------------------------------------------------------
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            nombre,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
