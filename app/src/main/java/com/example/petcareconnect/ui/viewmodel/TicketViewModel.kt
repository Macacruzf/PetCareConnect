package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.petcareconnect.data.model.Ticket
import com.example.petcareconnect.data.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class TicketUiState(
    val tickets: List<Ticket> = emptyList(),
    val usuarioId: Int = 0,
    val tipo: String = "",
    val comentario: String = "",
    val fecha: String = "",
    val estado: String = "Pendiente",
    val successMsg: String? = null,
    val errorMsg: String? = null
)

class TicketViewModel(private val repository: TicketRepository) : ViewModel() {

    private val _state = MutableStateFlow(TicketUiState())
    val state: StateFlow<TicketUiState> = _state

    init {
        loadTickets()
    }

    // Cargar todos los tickets
    private fun loadTickets() {
        viewModelScope.launch {
            repository.getAllTickets().collect { lista ->
                _state.value = _state.value.copy(tickets = lista)
            }
        }
    }

    // Manejo de campos
    fun onTipoChange(value: String) {
        _state.value = _state.value.copy(tipo = value)
    }

    fun onComentarioChange(value: String) {
        _state.value = _state.value.copy(comentario = value)
    }

    fun onUsuarioChange(value: Int) {
        _state.value = _state.value.copy(usuarioId = value)
    }

    // Insertar nuevo ticket
    fun guardarTicket() {
        val tipo = _state.value.tipo
        val comentario = _state.value.comentario
        val usuarioId = _state.value.usuarioId

        if (tipo.isBlank() || comentario.isBlank() || usuarioId == 0) {
            _state.value = _state.value.copy(errorMsg = "Completa todos los campos antes de enviar.")
            return
        }

        val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

        viewModelScope.launch {
            try {
                val nuevo = Ticket(
                    usuarioId = usuarioId,
                    tipo = tipo,
                    comentario = comentario,
                    fecha = fechaActual,
                    estado = "Pendiente"
                )
                repository.insert(nuevo)
                _state.value = _state.value.copy(
                    tipo = "",
                    comentario = "",
                    successMsg = "Ticket enviado correctamente",
                    errorMsg = null
                )
                loadTickets()
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMsg = "Error al guardar ticket: ${e.message}")
            }
        }
    }

    // Cambiar estado
    fun marcarComoResuelto(id: Int) {
        viewModelScope.launch {
            repository.updateEstado(id, "Resuelto")
        }
    }

    fun clearMessages() {
        _state.value = _state.value.copy(successMsg = null, errorMsg = null)
    }
}

class TicketViewModelFactory(private val repository: TicketRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TicketViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TicketViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
