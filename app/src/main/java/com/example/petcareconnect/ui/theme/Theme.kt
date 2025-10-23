package com.example.petcareconnect.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

//  PALETA DE COLORES PETCARE CONNECT
val GreenPrimary = Color(0xFF4CAF50)   // Verde - color principal
val OrangeSecondary = Color(0xFFFF9800) // Naranja - color de énfasis
val BlueAccent = Color(0xFF2196F3)      // Azul - acento o info
val DarkGrayText = Color(0xFF333333)    // Texto principal
val WhiteBackground = Color(0xFFFFFFFF) // Fondo claro

// Modo oscuro (colores ajustados)
val DarkPrimary = Color(0xFF2E7D32)
val DarkSecondary = Color(0xFFEF6C00)
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkOnSurface = Color(0xFFE0E0E0)

//  COLOR SCHEMES (light / dark)
private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = WhiteBackground,
    secondary = OrangeSecondary,
    onSecondary = WhiteBackground,
    tertiary = BlueAccent,
    background = WhiteBackground,
    onBackground = DarkGrayText,
    surface = Color(0xFFF7F7F7),
    onSurface = DarkGrayText
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = WhiteBackground,
    secondary = DarkSecondary,
    onSecondary = WhiteBackground,
    tertiary = BlueAccent,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface
)


//  TEMA PETCARE CONNECT

@Composable
fun PetCareConnectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Si está activado el modo dinámico (Android 12+)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Si el sistema está en modo oscuro
        darkTheme -> DarkColorScheme
        // Caso normal (modo claro)
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Puedes dejar la tipografía por defecto o personalizarla
        content = content
    )
}