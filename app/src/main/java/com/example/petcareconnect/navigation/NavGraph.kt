package com.example.petcareconnect.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import com.example.petcareconnect.ui.components.*
import com.example.petcareconnect.ui.screen.*
import com.example.petcareconnect.ui.viewmodel.*
import com.example.petcareconnect.data.model.Carrito
import com.google.gson.Gson
import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.platform.LocalContext

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    productoViewModel: ProductoViewModel
) {
    // Estado del Drawer lateral (abierto/cerrado)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // Alcance de corrutinas para ejecutar animaciones o cambios de estado asíncronos
    val scope = rememberCoroutineScope()
    // Estado para manejar mensajes flotantes (snackbar)
    val snackbarHostState = remember { SnackbarHostState() }

    // Se obtiene el rol actual del usuario y sus datos desde el ViewModel de autenticación
    val userRole by authViewModel.userRole.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // ViewModels compartidos entre pantallas
    val carritoViewModel: CarritoViewModel = viewModel()
    val pedidosViewModel: PedidosViewModel = viewModel()

    // --- Funciones para navegar entre pantallas ---
    val goHome = { navController.navigate(Route.Home.path) }
    val goLogin = { navController.navigate(Route.Login.path) }
    val goRegister = { navController.navigate(Route.Register.path) }
    val goProductos = { navController.navigate(Route.Productos.path) }
    val goCategorias = { navController.navigate(Route.Categorias.path) }
    val goHistorial = { navController.navigate(Route.HistorialVentas.path) }
    val goUsuarios = { navController.navigate(Route.Usuarios.path) }
    val goCarrito = { navController.navigate(Route.Carrito.path) }
    val goPedidos = { navController.navigate(Route.PedidosClientes.path) }

    // Drawer principal de navegación lateral
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Contenido del menú lateral con botones de navegación
            AppDrawer(
                currentRoute = navController.currentBackStackEntry?.destination?.route,
                items = defaultDrawerItems(
                    onHome = {
                        // Se cierra el drawer con animación (scope.launch)
                        scope.launch { drawerState.close() }
                        goHome()
                    },
                    onLogin = {
                        scope.launch { drawerState.close() }
                        goLogin()
                    },
                    onRegister = {
                        scope.launch { drawerState.close() }
                        goRegister()
                    }
                )
            )
        }
    ) {
        // Estructura visual principal de cada pantalla
        Scaffold(
            topBar = {
                // Barra superior con icono de menú y navegación
                AppTopBar(
                    onOpenDrawer = { scope.launch { drawerState.open() } }, // animación de apertura lateral
                    onHome = goHome,
                    onLogin = goLogin,
                    onRegister = goRegister
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) } // Contenedor de mensajes emergentes animados
        ) { innerPadding ->

            // Controlador principal de rutas (NavHost)
            NavHost(
                navController = navController,
                startDestination = Route.Home.path,
                modifier = Modifier.padding(innerPadding)
            ) {

                // Pantalla de inicio
                composable(Route.Home.path) {
                    val state by productoViewModel.state.collectAsState()

                    // Efecto que se ejecuta al entrar en la pantalla
                    // LaunchedEffect es una animación controlada por corrutina (dispara tareas iniciales)
                    LaunchedEffect(Unit) {
                        if (state.categorias.isEmpty()) {
                            productoViewModel.recargarCategoriasManualmente()
                        }
                    }

                    HomeScreen(
                        rol = userRole,
                        usuarioNombre = currentUser?.nombreUsuario,
                        productos = state.productos,
                        categorias = state.categorias,
                        onGoLogin = goLogin,
                        onGoRegister = goRegister,
                        onAgregarAlCarrito = { producto ->
                            // Agrega el producto al carrito
                            carritoViewModel.agregarItem(
                                Carrito(
                                    idProducto = producto.idProducto,
                                    nombre = producto.nombre,
                                    precio = producto.precio,
                                    cantidad = 1,
                                    imagenResId = producto.imagenResId
                                )
                            )
                            // Muestra un snackbar con animación
                            scope.launch {
                                snackbarHostState.showSnackbar("✅ ${producto.nombre} agregado al carrito")
                            }
                        },
                        onGoCategorias = goCategorias,
                        onGoUsuarios = goUsuarios,
                        onGoHistorial = goHistorial,
                        onAgregarProducto = { goProductos() },
                        onGoCarrito = goCarrito,
                        onGoPedidos = goPedidos,
                        onLogout = {
                            authViewModel.logout()
                            navController.navigate(Route.Home.path)
                        }
                    )
                }

                // Pantalla de Login
                composable(Route.Login.path) {
                    LoginScreenVm(
                        viewModel = authViewModel,
                        onLoginOkNavigateHome = goHome,
                        onGoRegister = goRegister
                    )
                }

                // Pantalla de Registro
                composable(Route.Register.path) {
                    RegisterScreenVm(
                        vm = authViewModel,
                        onRegisteredNavigateLogin = goLogin,
                        onGoLogin = goLogin
                    )
                }

                // Pantalla de Productos (solo visible si el rol lo permite)
                composable(Route.Productos.path) {
                    ProductoScreen(rol = userRole)
                }

                // Pantalla de Categorías
                composable(Route.Categorias.path) { CategoriaScreen() }

                // Pantalla del Carrito
                composable(Route.Carrito.path) {
                    CarritoScreen(
                        viewModel = carritoViewModel,
                        onConfirmarCompra = {
                            navController.navigate(Route.Pago.path)
                        }
                    )
                }

                // Pantalla de selección de método de pago
                composable(Route.Pago.path) {
                    PagoScreen(
                        carritoViewModel = carritoViewModel,
                        onEfectivoOTransferencia = {
                            navController.navigate("${Route.PagoEnTienda.path}/Efectivo")
                        },
                        onTarjeta = {
                            navController.navigate("${Route.PagoTarjeta.path}/Tarjeta")
                        }
                    )
                }

                // Pago en tienda (efectivo o transferencia)
                composable(
                    route = "${Route.PagoEnTienda.path}/{metodoPago}",
                    arguments = listOf(navArgument("metodoPago") { type = NavType.StringType })
                ) { backStackEntry ->
                    val metodoPago = backStackEntry.arguments?.getString("metodoPago") ?: "Efectivo"

                    PagoEnTiendaScreen(onContinuar = {
                        // Registra pedido con animación de navegación
                        pedidosViewModel.registrarPedido(
                            carritoViewModel.state.value.items,
                            carritoViewModel.state.value.total,
                            metodoPago
                        )
                        val total = carritoViewModel.state.value.total
                        val itemsJson = Uri.encode(Gson().toJson(carritoViewModel.state.value.items))
                        // Navegación animada al detalle de venta
                        navController.navigate("${Route.DetalleVenta.path}/$total/$itemsJson/$metodoPago")
                        carritoViewModel.vaciarCarrito()
                    })
                }

                // Pago con tarjeta (simulación)
                composable(
                    route = "${Route.PagoTarjeta.path}/{metodoPago}",
                    arguments = listOf(navArgument("metodoPago") { type = NavType.StringType })
                ) { backStackEntry ->
                    val metodoPago = backStackEntry.arguments?.getString("metodoPago") ?: "Tarjeta"

                    SimulacionPagoScreen(onPagoExitoso = {
                        pedidosViewModel.registrarPedido(
                            carritoViewModel.state.value.items,
                            carritoViewModel.state.value.total,
                            metodoPago
                        )
                        val total = carritoViewModel.state.value.total
                        val itemsJson = Uri.encode(Gson().toJson(carritoViewModel.state.value.items))
                        navController.navigate("${Route.DetalleVenta.path}/$total/$itemsJson/$metodoPago")
                        carritoViewModel.vaciarCarrito()
                    })
                }

                // Pantalla de detalle de venta
                composable(
                    route = "${Route.DetalleVenta.path}/{total}/{itemsJson}/{metodoPago}",
                    arguments = listOf(
                        navArgument("total") { type = NavType.StringType },
                        navArgument("itemsJson") { type = NavType.StringType },
                        navArgument("metodoPago") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val total = backStackEntry.arguments?.getString("total")?.toDoubleOrNull() ?: 0.0
                    val itemsJson = backStackEntry.arguments?.getString("itemsJson")
                    val metodoPago = backStackEntry.arguments?.getString("metodoPago") ?: "Desconocido"
                    val items = Gson().fromJson(itemsJson, Array<Carrito>::class.java).toList()

                    DetalleVentaScreen(
                        total = total,
                        items = items,
                        metodoPago = metodoPago,
                        onFinalizar = { navController.navigate(Route.Home.path) }
                    )
                }

                // Pantalla de pedidos del cliente
                composable(Route.PedidosClientes.path) {
                    val context = LocalContext.current
                    val pedidosViewModel: PedidosViewModel = viewModel(
                        factory = ViewModelProvider.AndroidViewModelFactory(context.applicationContext as Application)
                    )

                    PedidosClienteScreen(
                        pedidosViewModel = pedidosViewModel,
                        rol = userRole,
                        onVolver = { navController.navigate(Route.Home.path) }
                    )
                }

                // Pantalla de historial de ventas
                composable(Route.HistorialVentas.path) {
                    val pedidosViewModel: PedidosViewModel = viewModel()

                    HistorialVentasScreen(
                        pedidosViewModel = pedidosViewModel,
                        onVolver = { navController.navigate(Route.Home.path) }
                    )
                }

                // Pantalla de usuarios
                composable(Route.Usuarios.path) { UsuarioScreen() }
            }
        }
    }
}
