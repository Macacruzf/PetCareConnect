package com.example.petcareconnect.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.petcareconnect.data.session.UserSession
import com.example.petcareconnect.ui.components.FotoPerfilSelector
import com.example.petcareconnect.ui.theme.PetGreenPrimary
import com.example.petcareconnect.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilUsuarioScreen(
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit
) {
    val fotoPerfil by authViewModel.fotoPerfil.collectAsState()
    val isUploading by authViewModel.isUploadingFoto.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Cargar foto de perfil al entrar
    LaunchedEffect(Unit) {
        authViewModel.cargarFotoPerfil()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PetGreenPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título
            Text(
                text = "Foto de Perfil",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PetGreenPrimary
            )

            // Selector de foto
            FotoPerfilSelector(
                fotoPerfil = fotoPerfil,
                onFotoSeleccionada = { bitmap ->
                    authViewModel.subirFotoPerfil(bitmap) { error ->
                        if (error == null) {
                            showSuccessDialog = true
                        } else {
                            errorMessage = error
                            showErrorDialog = true
                        }
                    }
                },
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Indicador de carga
            if (isUploading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(8.dp),
                    color = PetGreenPrimary
                )
                Text(
                    text = "Subiendo foto...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            // Información del usuario
            currentUser?.let { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Información Personal",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PetGreenPrimary
                        )

                        InfoRow("Nombre", user.nombreUsuario)
                        InfoRow("Email", user.email)
                        InfoRow("Teléfono", user.telefono)
                        InfoRow("Rol", user.rol)
                    }
                }
            }

            // Botón eliminar foto
            if (fotoPerfil != null) {
                OutlinedButton(
                    onClick = {
                        authViewModel.eliminarFotoPerfil { error ->
                            if (error == null) {
                                showSuccessDialog = true
                            } else {
                                errorMessage = error
                                showErrorDialog = true
                            }
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFD32F2F)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Eliminar foto de perfil")
                }
            }
        }
    }

    // Diálogo de éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Éxito") },
            text = { Text("Foto de perfil actualizada correctamente") },
            confirmButton = {
                TextButton(onClick = { showSuccessDialog = false }) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Diálogo de error
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}

