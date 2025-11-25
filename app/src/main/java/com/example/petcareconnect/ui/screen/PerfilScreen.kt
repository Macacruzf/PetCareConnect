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

    var email by remember { mutableStateOf(usuario.email) }
    var telefono by remember { mutableStateOf(usuario.telefono) }
    var passNueva by remember { mutableStateOf("") }

    var isSaving by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        Text(
            "Mi Perfil",
            style = MaterialTheme.typography.headlineSmall
        )

        // ---------- Nombre bloqueado ----------
        OutlinedTextField(
            value = usuario.nombreUsuario,
            onValueChange = {},
            label = { Text("Nombre (no editable)") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            enabled = false
        )

        // ---------- Email editable ----------
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth()
        )

        // ---------- Teléfono editable ----------
        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth()
        )

        // ---------- Contraseña nueva (opcional) ----------
        OutlinedTextField(
            value = passNueva,
            onValueChange = { passNueva = it },
            label = { Text("Nueva contraseña (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        errorMsg?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                isSaving = true

                authViewModel.updateUser(
                    email = email,
                    telefono = telefono,
                    password = if (passNueva.isBlank()) null else passNueva
                ) { error ->
                    isSaving = false
                    if (error != null) {
                        errorMsg = error
                    } else {
                        onVolver()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Guardar cambios")
            }
        }

        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
