package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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

/*
 * Pantalla de gestión de categorías de productos.
 * Permite crear, listar, editar y eliminar categorías almacenadas en la base de datos local.
 */
@Composable
fun CategoriaScreen() {
    val context = LocalContext.current

    // Inicialización de la base de datos Room de forma persistente.
    // remember evita que se reconstruya en cada recomposición.
    val db = remember {
        Room.databaseBuilder(
            context,
            PetCareDatabase::class.java,
            "petcare_db"
        )
            .fallbackToDestructiveMigration() // Permite recrear la BD si cambia la estructura
            .build()
    }

    // Crea un repositorio para acceder al DAO de categorías
    val repository = remember { CategoriaRepository(db.categoriaDao()) }

    // Instancia del ViewModel asociado a esta pantalla, utilizando la fábrica personalizada
    val vm: CategoriaViewModel = viewModel(factory = CategoriaViewModelFactory(repository))

    // Estado observable del ViewModel (categorías, campo de texto, errores, etc.)
    val state by vm.state.collectAsState()

    // Variables de control del diálogo de edición
    var editDialog by remember { mutableStateOf(false) }
    var editingId by remember { mutableStateOf<Int?>(null) }
    var editNombre by remember { mutableStateOf("") }

    // Estructura principal de la pantalla con un botón flotante
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { vm.insertCategoria() }, // Inserta una nueva categoría
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Agregar categoría",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Título de la pantalla
            Text(
                text = "Gestión de Categorías",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))

            // Campo de texto para ingresar una nueva categoría
            OutlinedTextField(
                value = state.nombre,
                onValueChange = vm::onNombreChange,
                label = { Text("Nueva categoría") },
                modifier = Modifier.fillMaxWidth()
            )

            // Mensaje de error si el campo está vacío o inválido
            state.errorMsg?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Spacer(Modifier.height(16.dp))

            // Lista de categorías almacenadas
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
                            // Nombre de la categoría
                            Text(
                                cat.nombre,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )

                            // Botón para abrir el diálogo de edición
                            IconButton(onClick = {
                                editingId = cat.idCategoria
                                editNombre = cat.nombre
                                editDialog = true
                            }) {
                                Icon(
                                    Icons.Filled.Edit,
                                    contentDescription = "Editar",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }

                            // Botón para eliminar una categoría
                            IconButton(onClick = { vm.deleteCategoria(cat.idCategoria) }) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = "Eliminar",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }

        // Diálogo para editar una categoría existente
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
                    Button(onClick = {
                        // Actualiza la categoría en base de datos
                        if (editingId != null) {
                            vm.updateCategoria(editingId!!, editNombre)
                        }
                        editDialog = false
                    }) {
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
