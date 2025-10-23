package com.example.petcareconnect.navigation

sealed class Route(val path: String) {
    data object Home : Route("home")
    data object Login : Route("login")
    data object Register : Route("register")

    data object Productos : Route("productos")
    data object Categorias : Route("categorias")
    data object Estados : Route("estados")
    data object Ventas : Route("ventas")
    data object HistorialVentas : Route("historial_ventas")
    data object Tickets : Route("tickets")
    data object Usuarios : Route("usuarios")
}
