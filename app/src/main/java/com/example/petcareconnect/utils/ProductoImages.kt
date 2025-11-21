package com.example.petcareconnect.utils


import com.example.petcareconnect.R

fun getDrawableProducto(nombre: String): Int {

    val n = nombre.lowercase().trim()

    return when {

        // -----------------------
        // ALIMENTOS
        // -----------------------
        "dogchow" in n -> R.drawable.comida_perrodogchow
        "whiskas" in n -> R.drawable.comidawhiskas_gato
        "snack" in n && "pedigree" in n -> R.drawable.snack_dentalpedigree

        // -----------------------
        // ACCESORIOS
        // -----------------------
        "correa" in n -> R.drawable.correa_retractilazul
        "collar" in n -> R.drawable.collar_rojo
        "plato" in n -> R.drawable.plato_doble

        // -----------------------
        // HIGIENE
        // -----------------------
        "shampoo" in n -> R.drawable.shampoo_gato
        "toallitas" in n -> R.drawable.toallitas_petclean
        "cortaúñas" in n || "cortaunias" in n -> R.drawable.cortaunias

        // -----------------------
        // SALUD
        // -----------------------
        "vitamina" in n -> R.drawable.vitaminas_vitac
        "nexgard" in n -> R.drawable.antipulgasnexgard
        "antipulgas" in n -> R.drawable.collar_antipulgas

        // -----------------------
        // JUGUETES
        // -----------------------
        "pelota" in n -> R.drawable.juguete_goma
        "ratón" in n || "raton" in n -> R.drawable.raton_tela
        "cuerda" in n -> R.drawable.cuerda_mordedora

        // DEFAULT
        else -> R.drawable.icon_default
    }
}
