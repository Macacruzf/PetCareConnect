package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
// import androidx.compose.material.icons.filled.Edit // ⛔ REMOVIDO: Edición deshabilitada
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.petcareconnect.data.model.Usuario
import com.example.petcareconnect.ui.theme.PetBlueAccent
import com.example.petcareconnect.ui.theme.PetGreenPrimary
import com.example.petcareconnect.ui.viewmodel.AuthViewModel

@Composable
fun UsuarioScreen(
    authViewModel: AuthViewModel,
    @Suppress("UNUSED_PARAMETER") onEditarUsuario: (Usuario) -> Unit = {}
) {
    val usuarios by authViewModel.allUsers.collectAsState()

    // Cargar usuarios al entrar
    LaunchedEffect(Unit) { authViewModel.cargarUsuarios() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))  // Fondo institucional
            .padding(16.dp)
    ) {

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // TÍTULO
            Text(
                text = "Usuarios Registrados",
                style = MaterialTheme.typography.headlineSmall,
                color = PetGreenPrimary
            )

            if (usuarios.isEmpty()) {
                Text(
                    "No hay usuarios registrados.",
                    color = Color.Gray
                )
            } else {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxHeight()
                ) {

                    items(usuarios) { user ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                // INFORMACIÓN
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Text(
                                        text = user.nombreUsuario,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color(0xFF333333)
                                    )

                                    Text(
                                        text = user.email,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )

                                    Text(
                                        text = "Rol: ${user.rol}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = PetBlueAccent
                                    )
                                }

                                // ACCIONES
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {

                                    // ⛔ EDITAR DESHABILITADO: El admin no puede modificar usuarios
                                    // IconButton(onClick = { onEditarUsuario(user) }) {
                                    //     Icon(
                                    //         Icons.Default.Edit,
                                    //         contentDescription = "Editar usuario",
                                    //         tint = PetGreenPrimary
                                    //     )
                                    // }

                                    IconButton(
                                        onClick = { authViewModel.deleteUser(user.idUsuario) }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Eliminar usuario",
                                            tint = Color(0xFFD32F2F) // rojo más elegante
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
