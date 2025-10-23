package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.petcareconnect.data.db.PetCareDatabase
import com.example.petcareconnect.data.repository.TicketRepository
import com.example.petcareconnect.ui.viewmodel.TicketViewModel
import com.example.petcareconnect.ui.viewmodel.TicketViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreen(usuarioId: Int = 1) { // Puedes reemplazar 1 por el usuario logeado
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(
            context,
            PetCareDatabase::class.java,
            "petcare_db"
        ).build()
    }

    val repository = remember { TicketRepository(db.ticketDao()) }
    val vm: TicketViewModel = viewModel(factory = TicketViewModelFactory(repository))
    val state by vm.state.collectAsState()

    LaunchedEffect(usuarioId) {
        vm.onUsuarioChange(usuarioId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Centro de Ayuda", style = MaterialTheme.typography.headlineSmall)

            // Tipo de ticket
            val tipos = listOf("Reclamo", "Felicitación", "Sugerencia")
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = state.tipo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de ticket") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    tipos.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo) },
                            onClick = {
                                vm.onTipoChange(tipo)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Comentario
            OutlinedTextField(
                value = state.comentario,
                onValueChange = vm::onComentarioChange,
                label = { Text("Comentario") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = { vm.guardarTicket() },
                modifier = Modifier
                    .align(Alignment.End)
                    .height(48.dp)
            ) {
                Icon(Icons.Filled.Send, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Enviar")
            }

            state.successMsg?.let {
                Text(it, color = Color(0xFF2E7D32))
            }
            state.errorMsg?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Divider(Modifier.padding(vertical = 8.dp))
            Text("Historial de Tickets", style = MaterialTheme.typography.titleMedium)

            LazyColumn {
                items(state.tickets) { ticket ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Tipo: ${ticket.tipo}", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                            Text("Comentario: ${ticket.comentario}")
                            Text("Fecha: ${ticket.fecha}")
                            Text("Estado: ${ticket.estado}")
                        }
                    }
                }
            }
        }
    }
}
