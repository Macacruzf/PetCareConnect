package com.example.petcareconnect.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.launch

// =============================================================
// VARIABLES TEMPORALES PARA LA VENTA (SE GUARDAN ANTES DE LIMPIAR EL CARRITO)
// =============================================================
private var compraItemsTemp: List<Carrito> = emptyList()
private var compraTotalTemp: Double = 0.0
private var compraMetodoTemp: String = "No especificado"


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


    // ------------------------------------------------------------
    // NAVEGADORES
    // ------------------------------------------------------------
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


    // =============================================================
    // DRAWER + TOPBAR
    // =============================================================
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = navController.currentBackStackEntry?.destination?.route,
                items = defaultDrawerItems(
                    userRole = userRole,
                    isLoggedIn = currentUser != null,

                    onHome = {
                        scope.launch { drawerState.close() }
                        goHome()
                    },
                    onProductos = {
                        scope.launch { drawerState.close() }
                        goProductos()
                    },
                    onCategorias = {
                        scope.launch { drawerState.close() }
                        goCategorias()
                    },
                    onUsuarios = {
                        scope.launch { drawerState.close() }
                        goUsuarios()
                    },
                    onCarrito = {
                        scope.launch { drawerState.close() }
                        goCarrito()
                    },
                    onPedidos = {
                        scope.launch { drawerState.close() }
                        goPedidos()
                    },
                    onLogin = {
                        scope.launch { drawerState.close() }
                        goLogin()
                    },
                    onRegister = {
                        scope.launch { drawerState.close() }
                        goRegister()
                    },
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
                    onPerfil = goPerfil,
                    onCarrito = goCarrito
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->


            // =============================================================
            // NAVHOST COMPLETO
            // =============================================================
            NavHost(
                navController = navController,
                startDestination = Route.Home.path,
                modifier = Modifier.padding(innerPadding)
            ) {


                // -- HOME --------------------------------------------------
                composable(Route.Home.path) {

                    val state by productoViewModel.state.collectAsState()

                    HomeScreen(
                        navController = navController,
                        rol = userRole,
                        usuarioNombre = currentUser?.nombreUsuario,
                        usuarioId = currentUser?.idUsuario?.toLong() ?: 0L,

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
                                    stock = producto.stock,
                                    imagenUrl = producto.imagenUrl
                                )
                            )
                            scope.launch { snackbarHostState.showSnackbar("Producto agregado") }
                        },

                        onGoCategorias = goCategorias,
                        onGoUsuarios = goUsuarios,
                        onGoHistorial = goHistorial,
                        onAgregarProducto = goProductos,
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

                            navController.navigate(Route.EditarProducto.path)
                        }
                    )
                }


                // --- LOGIN -----------------------------------------------
                composable(Route.Login.path) {
                    LoginScreenVm(
                        viewModel = authViewModel,
                        onLoginOkNavigateHome = goHome,
                        onGoRegister = goRegister
                    )
                }

                // --- REGISTRO -------------------------------------------
                composable(Route.Register.path) {
                    RegisterScreenVm(
                        vm = authViewModel,
                        onRegisteredNavigateLogin = goLogin,
                        onGoLogin = goLogin
                    )
                }

                // --- PERFIL ---------------------------------------------
                composable(Route.Perfil.path) {
                    PerfilScreen(
                        usuario = currentUser,
                        authViewModel = authViewModel,
                        onVolver = { navController.popBackStack() }
                    )
                }


                // --- CATEGORÍAS -----------------------------------------
                composable(Route.Categorias.path) {
                    CategoriaScreen()
                }

                // --- USUARIOS -------------------------------------------
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

                // --- EDITAR USUARIO ------------------------------------
                composable(Route.EditarUsuario.path) {
                    EditarUsuarioScreen(
                        idUsuario = navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.get<Int>("idUsuarioEditar") ?: 0,

                        authViewModel = authViewModel,
                        onVolver = { navController.popBackStack() }
                    )
                }


                // --- PRODUCTOS -----------------------------------------
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
                                    stock = producto.stock,
                                    imagenUrl = producto.imagenUrl
                                )
                            )
                            scope.launch { snackbarHostState.showSnackbar("Producto agregado") }
                        },

                        onEditarProducto = { producto ->
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("productoId", producto.idProducto)

                            navController.navigate(Route.EditarProducto.path)
                        }
                    )
                }


                // --- EDITAR PRODUCTO -----------------------------------
                composable(Route.EditarProducto.path) {

                    val productoIdLong = navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<Int>("productoId")
                        ?.toLong() ?: 0L

                    val state by productoViewModel.state.collectAsState()
                    val producto = state.productos.firstOrNull { it.idProducto == productoIdLong }

                    if (producto == null) {
                        Text("❌ Producto no encontrado")
                    } else {
                        EditarProductoScreen(
                            productoViewModel = productoViewModel,
                            producto = producto,
                            onVolver = { navController.popBackStack() }
                        )
                    }
                }


                // --- CARRITO --------------------------------------------
                composable(Route.Carrito.path) {
                    CarritoScreen(
                        viewModel = carritoViewModel,
                        onConfirmarCompra = {
                            navController.navigate(Route.Pago.path)
                        }
                    )
                }


                // --- MÉTODO DE PAGO -------------------------------------
                composable(Route.Pago.path) {
                    PagoScreen(
                        carritoViewModel = carritoViewModel,
                        onEfectivoOTransferencia = {

                            // Guardar datos ANTES de vaciar
                            val st = carritoViewModel.state.value
                            compraItemsTemp = st.items
                            compraTotalTemp = st.total
                            compraMetodoTemp = "Efectivo / Transferencia"

                            navController.navigate(Route.PagoEnTienda.path)
                        },
                        onTarjeta = {
                            navController.navigate(Route.PagoTarjeta.path)
                        }
                    )
                }


                // --- PAGO EN TIENDA -------------------------------------
                composable(Route.PagoEnTienda.path) {
                    PagoEnTiendaScreen(
                        onContinuar = {

                            val st = carritoViewModel.state.value

                            // Guardar datos temporales
                            compraItemsTemp = st.items
                            compraTotalTemp = st.total
                            compraMetodoTemp = "Pago en tienda"

                            // ⭐ REGISTRAR PEDIDO
                            pedidosViewModel.registrarPedido(
                                items = compraItemsTemp,
                                total = compraTotalTemp,
                                metodoPago = compraMetodoTemp
                            )

                            carritoViewModel.vaciarCarrito()

                            navController.navigate(Route.DetalleVenta.path)
                        }
                    )
                }



                // --- SIMULACIÓN TARJETA ---------------------------------
                composable(Route.PagoTarjeta.path) {
                    SimulacionPagoScreen(
                        carritoViewModel = carritoViewModel,
                        productoViewModel = productoViewModel,
                        onPagoExitoso = {

                            val st = carritoViewModel.state.value

                            // Guardar datos temporales
                            compraItemsTemp = st.items
                            compraTotalTemp = st.total
                            compraMetodoTemp = "Tarjeta"

                            // ⭐ REGISTRAR PEDIDO
                            pedidosViewModel.registrarPedido(
                                items = compraItemsTemp,
                                total = compraTotalTemp,
                                metodoPago = compraMetodoTemp
                            )

                            carritoViewModel.vaciarCarrito()

                            navController.navigate(Route.DetalleVenta.path)
                        }
                    )
                }



                // --- DETALLE DE LA VENTA FINAL --------------------------
                composable(Route.DetalleVenta.path) {

                    DetalleVentaScreen(
                        total = compraTotalTemp,
                        items = compraItemsTemp,
                        metodoPago = compraMetodoTemp,
                        onFinalizar = {
                            goHome()
                        }
                    )
                }


                // --- PEDIDOS DEL CLIENTE --------------------------------
                composable(Route.PedidosClientes.path) {
                    PedidosClienteScreen(
                        pedidosViewModel = pedidosViewModel,
                        rol = userRole ?: "CLIENTE",
                        onVolver = { navController.popBackStack() }
                    )
                }

            }
        }
    }
}
