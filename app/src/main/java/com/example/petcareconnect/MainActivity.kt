package com.example.petcareconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.petcareconnect.navigation.AppNavGraph
import com.example.petcareconnect.data.db.PetCareDatabase
import com.example.petcareconnect.data.repository.UsuarioRepository
import com.example.petcareconnect.ui.viewmodel.AuthViewModel
import com.example.petcareconnect.ui.viewmodel.AuthViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializamos la BD y el ViewModel
        val db = PetCareDatabase.getDatabase(applicationContext)
        val userRepo = UsuarioRepository(db.usuarioDao())
        val authViewModel = AuthViewModelFactory(userRepo).create(AuthViewModel::class.java)

        setContent {
            AppRoot(authViewModel)
        }
    }
}

@Composable
fun AppRoot(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavGraph(navController = navController, authViewModel = authViewModel)
        }
    }
}


