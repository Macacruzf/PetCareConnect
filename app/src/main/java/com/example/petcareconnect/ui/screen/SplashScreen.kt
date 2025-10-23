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
 * SplashScreen: pantalla de carga inicial con animación.
 * Muestra el logo de PetCare Connect y una transición al HomeScreen.
 */

@Composable
fun SplashScreen(navController: NavHostController) {
    val verde = Color(0xFF4CAF50)
    val fondo = Color(0xFFF5F5F5)

    // Animación de escala (efecto de entrada del logo)
    val scale = remember { Animatable(0f) }

    // Lanzamos la animación al iniciar
    LaunchedEffect(true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1200,
                easing = {
                    OvershootInterpolator(3f).getInterpolation(it)
                }
            )
        )
        delay(1800) // Espera antes de cambiar de pantalla
        navController.navigate("home") {
            popUpTo("splash") { inclusive = true } // Limpia el stack
        }
    }

    // Diseño visual del splash
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
            // Logo con animación de entrada
            Image(
                painter = painterResource(id = R.drawable.ic_petcare_logo),
                contentDescription = "Logo PetCare Connect",
                modifier = Modifier
                    .size((120 * scale.value).dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PetCare Connect",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = verde,
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "Cuidamos lo que más amas 🐾",
                color = Color(0xFF333333),
                fontSize = 16.sp
            )
        }
    }
}