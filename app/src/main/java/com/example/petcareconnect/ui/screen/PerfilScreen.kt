package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.petcareconnect.data.model.Usuario
import com.example.petcareconnect.ui.components.FotoPerfilSelector
import com.example.petcareconnect.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    val scope = rememberCoroutineScope()
    val fotoPerfil by authViewModel.fotoPerfil.collectAsState()
    val isUploadingFoto by authViewModel.isUploadingFoto.collectAsState()

    var email by remember { mutableStateOf(usuario.email) }
    var telefono by remember { mutableStateOf(usuario.telefono) }

    // Estados para cambio de contraseña
    var showChangePassword by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    var isSaving by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var successMsg by remember { mutableStateOf<String?>(null) }

    // Cargar foto de perfil al entrar
    LaunchedEffect(Unit) {
        authViewModel.cargarFotoPerfil()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            "Mi Perfil",
            style = MaterialTheme.typography.headlineSmall
        )

        // ========== FOTO DE PERFIL ==========
        FotoPerfilSelector(
            fotoPerfil = fotoPerfil,
            onFotoSeleccionada = { bitmap ->
                authViewModel.subirFotoPerfil(bitmap) { error ->
                    if (error == null) {
                        successMsg = "Foto de perfil actualizada"
                        scope.launch {
                            delay(2000)
                            successMsg = null
                        }
                    } else {
                        errorMsg = error
                    }
                }
            },
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if (isUploadingFoto) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
            Text("Subiendo foto...", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(8.dp))

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

        Spacer(Modifier.height(8.dp))

        // ---------- Botón para mostrar/ocultar cambio de contraseña ----------
        OutlinedButton(
            onClick = {
                showChangePassword = !showChangePassword
                if (!showChangePassword) {
                    // Limpiar campos al ocultar
                    currentPassword = ""
                    newPassword = ""
                    confirmNewPassword = ""
                    errorMsg = null
                    successMsg = null
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (showChangePassword) "Cancelar cambio de contraseña" else "Cambiar contraseña")
        }

        // ---------- Campos de cambio de contraseña (mostrar solo si showChangePassword = true) ----------
        if (showChangePassword) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Cambiar Contraseña",
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Contraseña actual") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Nueva contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    OutlinedTextField(
                        value = confirmNewPassword,
                        onValueChange = { confirmNewPassword = it },
                        label = { Text("Confirmar nueva contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Button(
                        onClick = {
                            isSaving = true
                            errorMsg = null
                            successMsg = null

                            authViewModel.changePassword(
                                currentPassword = currentPassword,
                                newPassword = newPassword,
                                confirmNewPassword = confirmNewPassword
                            ) { error ->
                                isSaving = false
                                if (error != null) {
                                    errorMsg = error
                                } else {
                                    successMsg = "Contraseña cambiada exitosamente"
                                    // Limpiar campos
                                    currentPassword = ""
                                    newPassword = ""
                                    confirmNewPassword = ""
                                    // Ocultar formulario después de 2 segundos
                                    scope.launch {
                                        delay(2000)
                                        showChangePassword = false
                                        successMsg = null
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSaving &&
                                  currentPassword.isNotBlank() &&
                                  newPassword.isNotBlank() &&
                                  confirmNewPassword.isNotBlank()
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Cambiar contraseña")
                        }
                    }
                }
            }
        }

        // Mensajes de éxito/error
        successMsg?.let {
            Text(text = it, color = MaterialTheme.colorScheme.primary)
        }

        errorMsg?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(12.dp))

        // ---------- Guardar cambios de perfil ----------
        Button(
            onClick = {
                isSaving = true
                errorMsg = null
                successMsg = null

                authViewModel.updateUser(
                    email = email,
                    telefono = telefono,
                    password = null  // Ya no se usa para cambio de contraseña
                ) { error ->
                    isSaving = false
                    if (error != null) {
                        errorMsg = error
                    } else {
                        successMsg = "Perfil actualizado exitosamente"
                        scope.launch {
                            delay(1500)
                            onVolver()
                        }
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
                Text("Guardar cambios de perfil")
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
