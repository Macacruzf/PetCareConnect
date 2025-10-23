// 📦 AppNavGraph.kt
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

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val loginState by authViewModel.login.collectAsState() // 👈 escuchamos cambios de login

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
                // ✅ Home conectado al AuthViewModel
                composable(Route.Home.path) {
                    HomeScreen(
                        rol = loginState.rol,
                        onGoLogin = goLogin,
                        onGoRegister = goRegister,
                        onGoProductos = goProductos,
                        onGoCategorias = goCategorias,
                        onGoHistorial = goHistorial,
                        onGoUsuarios = goUsuarios
                    )
                }

                composable(Route.Login.path) {
                    LoginScreenVm(
                        onLoginOkNavigateHome = goHome,
                        onGoRegister = goRegister
                    )
                }

                composable(Route.Register.path) {
                    RegisterScreenVm(
                        onRegisteredNavigateLogin = goLogin,
                        onGoLogin = goLogin
                    )
                }

                composable(Route.Productos.path) { ProductoScreen() }
                composable(Route.Categorias.path) { CategoriaScreen() }
                composable(Route.HistorialVentas.path) { HistorialVentasScreen() }
                composable(Route.Usuarios.path) { UsuarioScreen() }
            }
        }
    }
}



