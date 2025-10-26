package com.example.petcareconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.petcareconnect.data.db.PetCareDatabase
import com.example.petcareconnect.data.repository.UsuarioRepository
import com.example.petcareconnect.data.repository.ProductoRepository
import com.example.petcareconnect.data.repository.CategoriaRepository
import com.example.petcareconnect.ui.viewmodel.AuthViewModel
import com.example.petcareconnect.ui.viewmodel.AuthViewModelFactory
import com.example.petcareconnect.ui.viewmodel.ProductoViewModel
import com.example.petcareconnect.ui.viewmodel.ProductoViewModelFactory
import com.example.petcareconnect.ui.screen.AppRootScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ✅ Inicializa la base de datos
        val db = PetCareDatabase.getDatabase(applicationContext)

        // ✅ Crea los repositorios
        val usuarioRepo = UsuarioRepository(db.usuarioDao())
        val productoRepo = ProductoRepository(db.productoDao())
        val categoriaRepo = CategoriaRepository(db.categoriaDao())

        // ✅ Crea los ViewModels
        val authViewModel = AuthViewModelFactory(usuarioRepo).create(AuthViewModel::class.java)
        val productoViewModel =
            ProductoViewModelFactory(productoRepo, categoriaRepo).create(ProductoViewModel::class.java)

        // ✅ Renderiza la aplicación pasando los ViewModels
        setContent {
            AppRootScreen(
                authViewModel = authViewModel,
                productoViewModel = productoViewModel
            )
        }
    }
}
