package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.petcareconnect.data.model.Usuario
import com.example.petcareconnect.ui.viewmodel.AuthViewModel

@Composable
fun EditarUsuarioScreen(
    idUsuario: Int,
    authViewModel: AuthViewModel,
    onVolver: () -> Unit
) {
    val usuarios by authViewModel.allUsers.collectAsState()
    val usuario = usuarios.firstOrNull { it.idUsuario == idUsuario }

    if (usuario == null) {
        Text("⚠ Usuario no encontrado")
        return
    }

    var nombre by remember { mutableStateOf(usuario.nombreUsuario) }
    var email by remember { mutableStateOf(usuario.email) }
    var telefono by remember { mutableStateOf(usuario.telefono ?: "") }
    var pass by remember { mutableStateOf(usuario.password) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text("Editar usuario", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val actualizado = usuario.copy(
                    nombreUsuario = nombre,
                    email = email,
                    telefono = telefono,
                    password = pass
                )

                authViewModel.updateUserAdmin(actualizado)
                onVolver()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar cambios")
        }

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}
