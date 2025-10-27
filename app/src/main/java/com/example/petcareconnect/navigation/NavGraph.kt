package com.example.petcareconnect.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import com.example.petcareconnect.ui.components.*
import com.example.petcareconnect.ui.screen.*
import com.example.petcareconnect.ui.viewmodel.AuthViewModel
import com.example.petcareconnect.ui.viewmodel.ProductoViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    productoViewModel: ProductoViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val userRole by authViewModel.userRole.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // --- Navegaciones ---
    val goHome = { navController.navigate(Route.Home.path) }
    val goLogin = { navController.navigate(Route.Login.path) }
    val goRegister = { navController.navigate(Route.Register.path) }
    val goProductos = { navController.navigate(Route.Productos.path) }
    val goCategorias = { navController.navigate(Route.Categorias.path) }
    val goHistorial = { navController.navigate(Route.HistorialVentas.path) }
    val goUsuarios = { navController.navigate(Route.Usuarios.path) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                currentRoute = navController.currentBackStackEntry?.destination?.route,
                items = defaultDrawerItems(
                    onHome = {
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
        Scaffold(
            topBar = {
                AppTopBar(
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onHome = goHome,
                    onLogin = goLogin,
                    onRegister = goRegister
                )
            }
        ) { innerPadding ->

            // --- NAVEGACIÓN PRINCIPAL ---
            NavHost(
                navController = navController,
                startDestination = Route.Home.path,
                modifier = Modifier.padding(innerPadding)
            ) {

                // 🏠 HOME — muestra productos y categorías
                composable(Route.Home.path) {
                    val state by productoViewModel.state.collectAsState()

                    // ✅ Cargar productos y categorías al abrir el Home
                    LaunchedEffect(Unit) {
                        if (state.productos.isEmpty()) {
                            println("📦 Cargando productos iniciales desde Home...")
                            productoViewModel.loadProductos()
                        }
                        if (state.categorias.isEmpty()) {
                            println("📚 Cargando categorías iniciales desde Home...")
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
                            println("🛒 Producto agregado: ${producto.nombre}")
                        },
                        onGoCategorias = goCategorias,
                        onGoUsuarios = goUsuarios,
                        onGoHistorial = goHistorial,
                        onAgregarProducto = { goProductos() },
                        onLogout = {
                            authViewModel.logout()
                            navController.navigate(Route.Home.path)
                        }
                    )
                }

                // LOGIN
                composable(Route.Login.path) {
                    LoginScreenVm(
                        viewModel = authViewModel,
                        onLoginOkNavigateHome = { goHome() },
                        onGoRegister = goRegister
                    )
                }

                // REGISTRO
                composable(Route.Register.path) {
                    RegisterScreenVm(
                        vm = authViewModel,
                        onRegisteredNavigateLogin = goLogin,
                        onGoLogin = goLogin
                    )
                }

                // PRODUCTOS (solo admin puede modificar)
                composable(Route.Productos.path) {
                    ProductoScreen(rol = userRole)
                }

                //  CATEGORÍAS
                composable(Route.Categorias.path) { CategoriaScreen() }

                // HISTORIAL DE VENTAS
                composable(Route.HistorialVentas.path) { HistorialVentasScreen() }

                //  USUARIOS
                composable(Route.Usuarios.path) { UsuarioScreen() }
            }
        }
    }
}
