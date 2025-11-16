package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.petcareconnect.data.db.PetCareDatabase
import com.example.petcareconnect.data.repository.CategoriaRepository
import com.example.petcareconnect.ui.viewmodel.CategoriaViewModel
import com.example.petcareconnect.ui.viewmodel.CategoriaViewModelFactory
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun CategoriaScreen() {
    val context = LocalContext.current

    val db = remember {
        Room.databaseBuilder(
            context,
            PetCareDatabase::class.java,
            "petcare_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    val repository = remember { CategoriaRepository(db.categoriaDao()) }
    val vm: CategoriaViewModel = viewModel(factory = CategoriaViewModelFactory(repository))
    val state by vm.state.collectAsState()

    var editDialog by remember { mutableStateOf(false) }
    var editingId by remember { mutableStateOf<Int?>(null) }
    var editNombre by remember { mutableStateOf("") }

    Scaffold { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                text = "Gestión de Categorías",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(20.dp))

            // ---------------------------
            //  INPUT + BOTÓN AGREGAR
            // ---------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = vm::onNombreChange,
                    label = { Text("Nueva categoría") },
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(12.dp))

                Button(
                    onClick = { vm.insertCategoria() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50), // Verde institucional
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(65.dp),        // <-- Botón más grande y visible
                    shape = RoundedCornerShape(14.dp)       // <-- Borde moderno y suave
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Agregar",
                        modifier = Modifier.size(34.dp)      // <-- Ícono que SÍ se ve
                    )
                }
            }



            // Error message
            state.errorMsg?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(Modifier.height(20.dp))

            // ---------------------------
            // LISTA DE CATEGORÍAS
            // ---------------------------
            LazyColumn {
                items(state.categorias) { cat ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                cat.nombre,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(onClick = {
                                editingId = cat.idCategoria
                                editNombre = cat.nombre
                                editDialog = true
                            }) {
                                Icon(
                                    Icons.Filled.Edit,
                                    contentDescription = "Editar",
                                    tint = Color(0xFF1976D2)
                                )
                            }

                            IconButton(onClick = { vm.deleteCategoria(cat.idCategoria) }) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Color(0xFFD32F2F)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ---------------------------
        // DIALOGO EDITAR CATEGORÍA
        // ---------------------------
        if (editDialog) {
            AlertDialog(
                onDismissRequest = { editDialog = false },
                title = { Text("Editar categoría") },
                text = {
                    OutlinedTextField(
                        value = editNombre,
                        onValueChange = { editNombre = it },
                        label = { Text("Nuevo nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            editingId?.let { vm.updateCategoria(it, editNombre) }
                            editDialog = false
                        }
                    ) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { editDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
