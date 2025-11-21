package com.example.petcareconnect.navigation

import android.app.Application
import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.petcareconnect.ui.components.AppDrawer
import com.example.petcareconnect.ui.components.AppTopBar
import com.example.petcareconnect.ui.components.defaultDrawerItems
import com.example.petcareconnect.ui.screen.*
import com.example.petcareconnect.ui.viewmodel.*
import com.example.petcareconnect.data.model.Carrito
import com.example.petcareconnect.navigation.Route
import com.google.gson.Gson
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    productoViewModel: ProductoViewModel,
    ticketViewModel: TicketViewModel
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentUser by authViewModel.currentUser.collectAsState()
    val userRole by authViewModel.userRole.collectAsState()

    val carritoViewModel: CarritoViewModel = viewModel()
    val pedidosViewModel: PedidosViewModel = viewModel()

    // Navegadores
    val goHome = { navController.navigate(Route.Home.path) }
    val goLogin = { navController.navigate(Route.Login.path) }
    val goRegister = { navController.navigate(Route.Register.path) }
    val goProductos = { navController.navigate(Route.Productos.path) }
    val goCategorias = { navController.navigate(Route.Categorias.path) }
    val goHistorial = { navController.navigate(Route.HistorialVentas.path) }
    val goUsuarios = { navController.navigate(Route.Usuarios.path) }
    val goCarrito = { navController.navigate(Route.Carrito.path) }
    val goPedidos = { navController.navigate(Route.PedidosClientes.path) }
    val goPerfil = { navController.navigate(Route.Perfil.path) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = navController.currentBackStackEntry?.destination?.route,
                items = defaultDrawerItems(
                    userRole = userRole,
                    isLoggedIn = currentUser != null,
                    onHome = { scope.launch { drawerState.close() }; goHome() },
                    onProductos = { scope.launch { drawerState.close() }; goProductos() },
                    onCategorias = { scope.launch { drawerState.close() }; goCategorias() },
                    onUsuarios = { scope.launch { drawerState.close() }; goUsuarios() },
                    onCarrito = { scope.launch { drawerState.close() }; goCarrito() },
                    onPedidos = { scope.launch { drawerState.close() }; goPedidos() },
                    onLogin = { scope.launch { drawerState.close() }; goLogin() },
                    onRegister = { scope.launch { drawerState.close() }; goRegister() },
                    onLogout = {
                        scope.launch { drawerState.close() }
                        authViewModel.logout()
                        goHome()
                    }
                )
            )
        }
    ) {

        Scaffold(
            topBar = {
                AppTopBar(
                    isLogged = currentUser != null,
                    rol = userRole ?: "INVITADO",
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onHome = goHome,
                    onLogin = goLogin,
                    onRegister = goRegister,
                    onPerfil = goPerfil
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->

            NavHost(
                navController = navController,
                startDestination = Route.Home.path,
                modifier = Modifier.padding(innerPadding)
            ) {

                // ============================
                // HOME
                // ============================
                composable(Route.Home.path) {

                    val state by productoViewModel.state.collectAsState()

                    HomeScreen(
                        rol = userRole,
                        usuarioNombre = currentUser?.nombreUsuario,
                        productos = state.productos,
                        categorias = state.categorias,
                        onGoLogin = goLogin,
                        onGoRegister = goRegister,
                        onAgregarAlCarrito = { producto ->
                            carritoViewModel.agregarItem(
                                Carrito(
                                    idProducto = producto.idProducto,
                                    nombre = producto.nombre,
                                    precio = producto.precio,
                                    cantidad = 1,
                                    imagenResId = producto.imagenResId
                                )
                            )
                            scope.launch { snackbarHostState.showSnackbar("Producto agregado") }
                        },
                        onGoCategorias = goCategorias,
                        onGoUsuarios = goUsuarios,
                        onGoHistorial = goHistorial,
                        onAgregarProducto = { goProductos() },
                        onGoCarrito = goCarrito,
                        onGoPedidos = goPedidos,
                        onLogout = {
                            authViewModel.logout()
                            goHome()
                        },
                        onCambiarEstado = { producto ->
                            productoViewModel.cambiarEstadoManual(
                                producto.idProducto,
                                producto.estado
                            )
                        },
                        onEditar = { producto ->
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("productoId", producto.idProducto)

                            navController.navigate("editarProducto")
                        }
                    )
                }

                // ============================
                // LOGIN
                // ============================
                composable(Route.Login.path) {
                    LoginScreenVm(
                        viewModel = authViewModel,
                        onLoginOkNavigateHome = goHome,
                        onGoRegister = goRegister
                    )
                }

                // ============================
                // REGISTRO
                // ============================
                composable(Route.Register.path) {
                    RegisterScreenVm(
                        vm = authViewModel,
                        onRegisteredNavigateLogin = goLogin,
                        onGoLogin = goLogin
                    )
                }

                // ============================
                // PERFIL
                // ============================
                composable(Route.Perfil.path) {
                    PerfilScreen(
                        usuario = currentUser,
                        authViewModel = authViewModel,
                        onVolver = goHome
                    )
                }

                // ============================
                // PRODUCTOS
                // ============================
                composable(Route.Productos.path) {
                    ProductoScreen(
                        rol = userRole,
                        productoViewModel = productoViewModel,
                        onAgregarAlCarrito = { producto ->
                            carritoViewModel.agregarItem(
                                Carrito(
                                    idProducto = producto.idProducto,
                                    nombre = producto.nombre,
                                    precio = producto.precio,
                                    cantidad = 1,
                                    imagenResId = producto.imagenResId
                                )
                            )
                            scope.launch { snackbarHostState.showSnackbar("Producto agregado") }
                        }
                    )
                }

                // ============================
                // EDITAR PRODUCTO
                // ============================
                composable("editarProducto") {

                    val productoId =
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.get<Long>("productoId")

                    val state by productoViewModel.state.collectAsState()
                    val producto = state.productos.firstOrNull { it.idProducto == productoId }

                    if (producto != null) {
                        EditarProductoScreen(
                            productoViewModel = productoViewModel,
                            producto = producto,
                            onVolver = { navController.popBackStack() }
                        )
                    } else {
                        Text("⚠ No se pudo cargar el producto.")
                    }
                }

                // ============================
                // CATEGORÍAS
                // ============================
                composable(Route.Categorias.path) {
                    CategoriaScreen()
                }

                // ============================
                // USUARIOS
                // ============================
                composable(Route.Usuarios.path) {
                    UsuarioScreen(
                        authViewModel = authViewModel,
                        onEditarUsuario = { usuario ->
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("idUsuarioEditar", usuario.idUsuario)

                            navController.navigate(Route.EditarUsuario.path)
                        }
                    )
                }

                // ============================
                // EDITAR USUARIO
                // ============================
                composable(Route.EditarUsuario.path) {

                    val id =
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.get<Int>("idUsuarioEditar")

                    if (id != null) {
                        EditarUsuarioScreen(
                            idUsuario = id,
                            authViewModel = authViewModel,
                            onVolver = { navController.popBackStack() }
                        )
                    } else {
                        Text("⚠ Error cargando usuario.")
                    }
                }

                // ============================
                // CARRITO
                // ============================
                composable(Route.Carrito.path) {
                    CarritoScreen(
                        viewModel = carritoViewModel,
                        onConfirmarCompra = { navController.navigate(Route.Pago.path) }
                    )
                }

                // ============================
                // PAGO
                // ============================
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

                // ============================
                // PAGO EN TIENDA
                // ============================
                composable("${Route.PagoEnTienda.path}/{metodoPago}") { entry ->
                    val metodoPago = entry.arguments?.getString("metodoPago") ?: "Efectivo"

                    PagoEnTiendaScreen(
                        onContinuar = {
                            pedidosViewModel.registrarPedido(
                                carritoViewModel.state.value.items,
                                carritoViewModel.state.value.total,
                                metodoPago
                            )

                            val total = carritoViewModel.state.value.total
                            val json = Uri.encode(Gson().toJson(carritoViewModel.state.value.items))

                            navController.navigate("${Route.DetalleVenta.path}/$total/$json/$metodoPago")

                            carritoViewModel.vaciarCarrito()
                        }
                    )
                }

                // ============================
                // DETALLE VENTA
                // ============================
                composable("${Route.DetalleVenta.path}/{total}/{itemsJson}/{metodoPago}") { entry ->

                    val total = entry.arguments?.getString("total")!!.toDouble()
                    val itemsJson = entry.arguments?.getString("itemsJson")
                    val metodoPago = entry.arguments?.getString("metodoPago")!!

                    val items = Gson().fromJson(itemsJson, Array<Carrito>::class.java).toList()

                    DetalleVentaScreen(
                        total = total,
                        items = items,
                        metodoPago = metodoPago,
                        onFinalizar = { navController.navigate(Route.Home.path) }
                    )
                }

                // ============================
                // HISTORIAL DE VENTAS
                // ============================
                composable(Route.HistorialVentas.path) {
                    HistorialVentasScreen(
                        pedidosViewModel = pedidosViewModel,
                        onVolver = goHome
                    )
                }

                // ============================
                // PEDIDOS CLIENTES
                // ============================
                composable(Route.PedidosClientes.path) {

                    val context = LocalContext.current

                    val pedidosVm: PedidosViewModel =
                        viewModel(
                            factory = ViewModelProvider.AndroidViewModelFactory(
                                context.applicationContext as Application
                            )
                        )

                    PedidosClienteScreen(
                        pedidosViewModel = pedidosVm,
                        rol = userRole,
                        onVolver = goHome
                    )
                }

                // ============================
                // DETALLE PRODUCTO + RESEÑAS
                // ============================
                composable("detalleProducto/{productoId}") { entry ->

                    val productoId = entry.arguments?.getString("productoId")?.toLongOrNull()
                    val state by productoViewModel.state.collectAsState()

                    // ⭐ Buscar producto asegurando conversión correcta
                    val producto = state.productos.firstOrNull { it.idProducto.toLong() == productoId }

                    // ⭐ Asegurar conversión de usuarioId a Long
                    val usuarioId = authViewModel.currentUser.value?.idUsuario?.toLong() ?: 0L

                    if (producto != null) {
                        ProductoDetalleScreen(
                            producto = producto,
                            usuarioId = usuarioId,   // ← ahora es Long REAL
                            vm = ticketViewModel
                        )
                    } else {
                        Text("❌ Producto no encontrado")
                    }
                }
            }
        }
    }
}
