package com.example.petcareconnect.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch
import com.example.petcareconnect.ui.components.*
import com.example.petcareconnect.ui.screen.*
import com.example.petcareconnect.ui.viewmodel.AuthViewModel
import com.example.petcareconnect.data.db.PetCareDatabase
import com.example.petcareconnect.data.repository.ProductoRepository
import com.example.petcareconnect.data.repository.CategoriaRepository
import com.example.petcareconnect.ui.viewmodel.ProductoViewModel
import com.example.petcareconnect.ui.viewmodel.ProductoViewModelFactory

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val loginState by authViewModel.login.collectAsState()

    // Rutas rápidas
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

                //  HOME ACTUALIZADO (Muestra productos)
                composable(Route.Home.path) {
                    val context = LocalContext.current
                    val db = remember {
                        androidx.room.Room.databaseBuilder(
                            context,
                            PetCareDatabase::class.java,
                            "petcare_db"
                        ).build()
                    }

                    val productoRepo = remember { ProductoRepository(db.productoDao()) }
                    val categoriaRepo = remember { CategoriaRepository(db.categoriaDao()) }
                    val productoVm: ProductoViewModel =
                        androidx.lifecycle.viewmodel.compose.viewModel(factory = ProductoViewModelFactory(productoRepo, categoriaRepo))
                    val state by productoVm.state.collectAsState()

                    HomeScreen(
                        rol = loginState.rol,
                        productos = state.productos, // 🔹 pasa la lista de productos al Home
                        onGoLogin = goLogin,
                        onGoRegister = goRegister,
                        onAgregarAlCarrito = { producto ->
                            println("🛒 Producto agregado al carrito: ${producto.nombre}")
                        },
                        onGoCategorias = goCategorias,
                        onGoUsuarios = goUsuarios,
                        onGoHistorial = goHistorial,
                        onAgregarProducto = {
                            //  Solo admin: redirige a la pantalla de productos
                            navController.navigate(Route.Productos.path)
                        }
                    )
                }

                //  LOGIN
                composable(Route.Login.path) {
                    LoginScreenVm(
                        onLoginOkNavigateHome = goHome,
                        onGoRegister = goRegister
                    )
                }

                //  REGISTRO
                composable(Route.Register.path) {
                    RegisterScreenVm(
                        onRegisteredNavigateLogin = goLogin,
                        onGoLogin = goLogin
                    )
                }

                //  PRODUCTOS (vista admin)
                composable(Route.Productos.path) {
                    ProductoScreen(rol = loginState.rol)
                }

                //  CATEGORÍAS
                composable(Route.Categorias.path) {
                    CategoriaScreen()
                }

                //  HISTORIAL DE VENTAS
                composable(Route.HistorialVentas.path) {
                    HistorialVentasScreen()
                }

                //  USUARIOS
                composable(Route.Usuarios.path) {
                    UsuarioScreen()
                }
            }
        }
    }
}



