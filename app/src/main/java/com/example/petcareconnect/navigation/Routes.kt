package com.example.petcareconnect.navigation

// Esta clase sellada define todas las rutas utilizadas por la navegación del sistema.
// Cada "data object" representa una pantalla o destino dentro del NavHost.
// Usar una clase sellada permite mantener el control centralizado y evitar errores de escritura en los nombres de rutas.

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

    // Pantalla de historial de ventas o pedidos realizados (para administradores)
    data object HistorialVentas : Route("historial_ventas")

    // Pantalla de gestión o listado de usuarios
    data object Usuarios : Route("usuarios")

    // Pantalla que muestra los productos añadidos al carrito de compras
    data object Carrito : Route("carrito")

    // Pantalla de selección del método de pago
    data object Pago : Route("pago")

    // Pantalla específica para pagos realizados en tienda (efectivo o transferencia)
    data object PagoEnTienda : Route("pago_tienda")

    // Pantalla de pago con tarjeta (simulación de pago electrónico)
    data object PagoTarjeta : Route("pago_tarjeta")

    // Pantalla que muestra el detalle final de la venta, incluyendo productos y total pagado
    data object DetalleVenta : Route("detalle_venta")

    // Pantalla que muestra los pedidos asociados a un cliente
    data object PedidosClientes : Route("pedidos_clientes")
}
