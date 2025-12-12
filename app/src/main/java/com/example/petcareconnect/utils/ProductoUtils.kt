package com.example.petcareconnect.utils

import com.example.petcareconnect.R

/**
 * Función de fallback temporal para mapear nombres de productos a drawables locales.
 * 
 * ⚠️ DEPRECATED: Esta función es solo un fallback temporal.
 * El objetivo final es que TODAS las imágenes vengan desde el backend (imagenUrl).
 * 
 * Solo se usa cuando el backend NO devuelve imagenUrl para un producto.
 */
fun getDrawableProducto(nombre: String): Int {
    return when {
        nombre.contains("DogChow", ignoreCase = true) -> R.drawable.comida_perrodogchow
        nombre.contains("Whiskas", ignoreCase = true) -> R.drawable.comidawhiskas_gato
        nombre.contains("Pedigree", ignoreCase = true) -> R.drawable.snack_dentalpedigree
        nombre.contains("Correa", ignoreCase = true) -> R.drawable.correa_retractilazul
        nombre.contains("Collar rojo", ignoreCase = true) -> R.drawable.collar_rojo
        nombre.contains("Collar antipulgas", ignoreCase = true) -> R.drawable.collar_antipulgas
        nombre.contains("Plato", ignoreCase = true) -> R.drawable.plato_doble
        nombre.contains("Shampoo", ignoreCase = true) -> R.drawable.shampoo_gato
        nombre.contains("Toallitas", ignoreCase = true) -> R.drawable.toallitas_petclean
        nombre.contains("Cortauñas", ignoreCase = true) -> R.drawable.cortaunias
        nombre.contains("Vitaminas", ignoreCase = true) -> R.drawable.vitaminas_vitac
        nombre.contains("NexGard", ignoreCase = true) -> R.drawable.antipulgasnexgard
        nombre.contains("Juguete", ignoreCase = true) -> R.drawable.juguete_goma
        nombre.contains("Ratón", ignoreCase = true) -> R.drawable.raton_tela
        nombre.contains("Cuerda", ignoreCase = true) -> R.drawable.cuerda_mordedora
        else -> R.drawable.productos_logo // Logo genérico por defecto
    }
}

