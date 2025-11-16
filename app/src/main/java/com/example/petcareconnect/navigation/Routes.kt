package com.example.petcareconnect.navigation

sealed class Route(val path: String) {

    // Pantalla principal de la aplicación (inicio)
    data object Home : Route("home")

    // Pantalla de inicio de sesión
    data object Login : Route("login")

    // Pantalla de registro de usuario
    data object Register : Route("register")

    // Pantalla de gestión o visualización de productos
    data object Productos : Route("productos")

    // Pantalla donde se muestran las categorías disponibles
    data object Categorias : Route("categorias")

    // Pantalla de historial de ventas (ADMIN)
    data object HistorialVentas : Route("historial_ventas")

    // Pantalla de gestión/listado de usuarios (ADMIN)
    data object Usuarios : Route("usuarios")

    // Pantalla que muestra el carrito del usuario
    data object Carrito : Route("carrito")

    // Pantalla para elegir método de pago
    data object Pago : Route("pago")

    // Pantalla de pago en tienda
    data object PagoEnTienda : Route("pago_tienda")

    // Pantalla de pago con tarjeta
    data object PagoTarjeta : Route("pago_tarjeta")

    // Pantalla de detalle final de una venta
    data object DetalleVenta : Route("detalle_venta")

    // Pantalla que muestra los pedidos del cliente
    data object PedidosClientes : Route("pedidos_clientes")

    // Pantalla del perfil del usuario
    data object Perfil : Route("perfil")

    // Pantalla de edición de usuario por administrador
    data object EditarUsuario : Route("editar_usuario")

}
