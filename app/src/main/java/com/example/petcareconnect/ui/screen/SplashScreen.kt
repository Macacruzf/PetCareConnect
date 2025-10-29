package com.example.petcareconnect.ui.screen

import android.view.animation.OvershootInterpolator
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.petcareconnect.R
import kotlinx.coroutines.delay
import androidx.compose.material3.Text
import androidx.compose.animation.core.*

/*
 -------------------------------------------------------------------------------
  PANTALLA DE INICIO (SPLASH SCREEN)
 -------------------------------------------------------------------------------
  Esta pantalla aparece brevemente al iniciar la aplicación PetCare Connect.
  Su objetivo es:
   - Mostrar el logotipo y nombre de la app de forma atractiva.
   - Aplicar una animación de entrada al logo (escala con rebote).
   - Transicionar automáticamente hacia la pantalla principal (HomeScreen)
     tras unos segundos de espera.

  Tecnologías y conceptos aplicados:
   - Jetpack Compose (UI declarativa).
   - Animaciones con `Animatable` y `tween()`.
   - `OvershootInterpolator` para efecto elástico.
   - Corrutinas y `LaunchedEffect` para temporización y navegación.
 -------------------------------------------------------------------------------
*/

@Composable
fun SplashScreen(navController: NavHostController) {

    // Colores de identidad visual de la app.
    val verde = Color(0xFF4CAF50)
    val fondo = Color(0xFFF5F5F5)

    // ------------------------------------------------------------------------
    // ANIMACIÓN DE ESCALA DEL LOGO
    // ------------------------------------------------------------------------
    // `Animatable` permite animar valores numéricos de forma suave.
    // Aquí se inicia con valor 0f (oculto) y crece hasta 1f (tamaño normal).
    // El efecto genera la sensación de “rebote” al finalizar.
    // ------------------------------------------------------------------------
    val scale = remember { Animatable(0f) }

    // ------------------------------------------------------------------------
    // EFECTO DE INICIO AUTOMÁTICO
    // ------------------------------------------------------------------------
    // Se ejecuta una sola vez al entrar en la pantalla.
    // 1. Ejecuta la animación de entrada del logo (duración: 1.2 segundos).
    // 2. Espera 1.8 segundos más para dar tiempo al usuario a ver el logo.
    // 3. Navega automáticamente al HomeScreen.
    // ------------------------------------------------------------------------
    LaunchedEffect(true) {
        // Animación de crecimiento con efecto “overshoot” (rebote suave).
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1200, // duración de 1.2 segundos
                easing = {
                    // OvershootInterpolator genera un rebote al final
                    OvershootInterpolator(3f).getInterpolation(it)
                }
            )
        )

        // Pausa breve para mantener el splash visible antes de navegar
        delay(1800)

        // Navegación automática al HomeScreen.
        // Se limpia el historial (popUpTo) para evitar volver al splash.
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true }
        }
    }

    // ------------------------------------------------------------------------
    // DISEÑO VISUAL DEL SPLASH SCREEN
    // ------------------------------------------------------------------------
    // Se utiliza una Box centrada que contiene el logotipo, el título
    // y un subtítulo. Todo se muestra sobre un fondo claro.
    // ------------------------------------------------------------------------
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(fondo),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // ----------------------------------------------------------------
            // LOGO CON ANIMACIÓN DE ENTRADA
            // ----------------------------------------------------------------
            // La imagen se escala dinámicamente con el valor animado de `scale`.
            // Al iniciar, el logo crece de 0 a 100% del tamaño original,
            // generando una sensación de aparición “con rebote”.
            // ----------------------------------------------------------------
            Image(
                painter = painterResource(id = R.drawable.ic_petcare_logo),
                contentDescription = "Logo PetCare Connect",
                modifier = Modifier
                    .size((120 * scale.value).dp) // Escala animada del logo
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ----------------------------------------------------------------
            // TEXTO PRINCIPAL (NOMBRE DE LA APLICACIÓN)
            // ----------------------------------------------------------------
            // Se muestra el nombre “PetCare Connect” en color verde institucional,
            // con una tipografía destacada y estilo Material 3.
            // ----------------------------------------------------------------
            Text(
                text = "PetCare Connect",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = verde,
                    fontWeight = FontWeight.Bold
                )
            )

            // ----------------------------------------------------------------
            // SUBTÍTULO
            // ----------------------------------------------------------------
            // Frase descriptiva que refuerza la identidad emocional de la app.
            // Se muestra con un tono gris oscuro y fuente más pequeña.
            // ----------------------------------------------------------------
            Text(
                text = "Cuidamos lo que más amas 🐾",
                color = Color(0xFF333333),
                fontSize = 16.sp
            )
        }
    }
}
