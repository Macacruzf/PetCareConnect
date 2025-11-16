package com.example.petcareconnect.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// -------------------------------------------------------------
// 🎨 ESQUEMA DE COLOR CLARO
// -------------------------------------------------------------
private val LightColors = lightColorScheme(
    primary = PetGreenPrimary,
    onPrimary = Color.White,
    secondary = PetOrangeSecondary,
    onSecondary = Color.White,
    tertiary = PetBlueAccent,

    background = PetLightBackground,
    onBackground = PetDarkGrayText,

    surface = Color.White,
    onSurface = PetDarkGrayText,
)

// -------------------------------------------------------------
// 🌙 ESQUEMA DE COLOR OSCURO
// -------------------------------------------------------------
private val DarkColors = darkColorScheme(
    primary = PetDarkPrimary,
    onPrimary = Color.White,
    secondary = PetDarkSecondary,
    onSecondary = Color.White,
    tertiary = PetBlueAccent,

    background = PetDarkBackground,
    onBackground = PetDarkOnSurface,

    surface = PetDarkSurface,
    onSurface = PetDarkOnSurface
)


// -------------------------------------------------------------
// 🐾 TEMA PRINCIPAL DE LA APP (CORREGIDO)
// -------------------------------------------------------------
@Composable
fun PetCareConnectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,   // ⛔ Desactivado para que NO use colores morados
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (darkTheme) DarkColors else LightColors   // SIEMPRE tus colores

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
