package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcareconnect.data.model.Ticket
import com.example.petcareconnect.data.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class TicketUiState(
    val tickets: List<Ticket> = emptyList(),
    val comentario: String = "",
    val calificacion: Int = 0,
    val successMsg: String? = null,
    val errorMsg: String? = null
)

class TicketViewModel(private val repo: TicketRepository) : ViewModel() {

    private val _state = MutableStateFlow(TicketUiState())
    val state: StateFlow<TicketUiState> = _state

    fun loadTickets(idProducto: Int) {
        viewModelScope.launch {
            repo.getTicketsByProducto(idProducto).collect { list ->
                _state.value = _state.value.copy(tickets = list)
            }
        }
    }

    fun onComentarioChange(value: String) {
        _state.value = _state.value.copy(comentario = value)
    }

    fun onCalificacionChange(value: Int) {
        _state.value = _state.value.copy(calificacion = value)
    }

    fun enviarTicket(idProducto: Int, usuario: String) {
        val c = _state.value.comentario.trim()
        val calif = _state.value.calificacion

        if (c.isEmpty() || calif == 0) {
            _state.value = _state.value.copy(errorMsg = "Completa comentario y calificación.")
            return
        }

        viewModelScope.launch {
            repo.insert(
                Ticket(
                    idProducto = idProducto,
                    nombreUsuario = usuario,
                    comentario = c,
                    calificacion = calif
                )
            )
            _state.value = _state.value.copy(
                comentario = "",
                calificacion = 0,
                successMsg = "¡Gracias por tu reseña!"
            )
        }
    }
}
