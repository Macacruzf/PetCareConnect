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
fun PerfilScreen(
    usuario: Usuario?,
    authViewModel: AuthViewModel,
    onVolver: () -> Unit
) {
    if (usuario == null) {
        Text("No hay datos de usuario.")
        return
    }

    // --- SOLO CAMPOS QUE EL CLIENTE PUEDE EDITAR ---
    var email by remember { mutableStateOf(usuario.email) }
    var telefono by remember { mutableStateOf(usuario.telefono ?: "") }
    var passNueva by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            "Mi Perfil",
            style = MaterialTheme.typography.headlineMedium
        )

        // 🔒 Nombre bloqueado
        OutlinedTextField(
            value = usuario.nombreUsuario,
            onValueChange = {},
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            enabled = false
        )

        // ✔ Email editable
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth()
        )

        // ✔ Teléfono editable
        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth()
        )

        // ✔ Contraseña editable opcional
        OutlinedTextField(
            value = passNueva,
            onValueChange = { passNueva = it },
            label = { Text("Nueva contraseña (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                authViewModel.updateUser(
                    nombre = null, // Cliente NO puede cambiar su nombre
                    email = email,
                    telefono = telefono,
                    pass = if (passNueva.isBlank()) null else passNueva
                )
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
            Text("Volver")
        }
    }
}
