package com.example.petcareconnect.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
    val loginState by authViewModel.login.collectAsState()
    val userRole by authViewModel.userRole.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // Navegaciones
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
            NavHost(
                navController = navController,
                startDestination = Route.Home.path,
                modifier = Modifier.padding(innerPadding)
            ) {

                // 🏠 HOME — muestra productos y opciones según el rol
                composable(Route.Home.path) {
                    val state by productoViewModel.state.collectAsState()

                    HomeScreen(
                        rol = userRole,
                        usuarioNombre = currentUser?.nombre,
                        productos = state.productos,
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

                // 🔑 LOGIN
                composable(Route.Login.path) {
                    LoginScreenVm(
                        viewModel = authViewModel,
                        onLoginOkNavigateHome = {
                            goHome()
                        },
                        onGoRegister = goRegister
                    )
                }

                // 📝 REGISTRO
                composable(Route.Register.path) {
                    RegisterScreenVm(
                        vm= authViewModel,
                        onRegisteredNavigateLogin = goLogin,
                        onGoLogin = goLogin
                    )
                }

                // 🧺 PRODUCTOS (solo admin puede modificar)
                composable(Route.Productos.path) {
                    ProductoScreen(rol = userRole)
                }

                composable(Route.Categorias.path) { CategoriaScreen() }
                composable(Route.HistorialVentas.path) { HistorialVentasScreen() }
                composable(Route.Usuarios.path) { UsuarioScreen() }
            }
        }
    }
}

