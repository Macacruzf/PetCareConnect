package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.petcareconnect.data.remote.dto.TicketRequest
import com.example.petcareconnect.data.remote.dto.TicketResponse
import com.example.petcareconnect.data.remote.repository.TicketRemoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// -------------------------------------------------------------
// ESTADO UI
// -------------------------------------------------------------
data class TicketUiState(
    val tickets: List<TicketResponse> = emptyList(),
    val comentario: String = "",
    val calificacion: Int = 0,
    val successMsg: String? = null,
    val errorMsg: String? = null
)

// -------------------------------------------------------------
// VIEWMODEL PRINCIPAL
// -------------------------------------------------------------
class TicketViewModel(
    private val repo: TicketRemoteRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TicketUiState())
    val state: StateFlow<TicketUiState> = _state

    // ⭐ Cambiar comentario
    fun onComentarioChange(value: String) {
        _state.value = _state.value.copy(comentario = value)
    }

    // ⭐ Cambiar cantidad de estrellas
    fun onCalificacionChange(value: Int) {
        _state.value = _state.value.copy(calificacion = value)
    }

    // ---------------------------------------------------------
    // ⭐ Cargar reseñas por producto
    // ---------------------------------------------------------
    fun loadTickets(idProducto: Long) {
        viewModelScope.launch {
            try {
                val lista = repo.listarPorProducto(idProducto)
                _state.value = _state.value.copy(tickets = lista)
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMsg = "Error al cargar reseñas.")
            }
        }
    }

    // ---------------------------------------------------------
    // ⭐ Enviar reseña
    // ---------------------------------------------------------
    fun enviarTicket(idProducto: Long, idUsuario: Long) {
        val comentario = _state.value.comentario.trim()
        val estrellas = _state.value.calificacion

        if (comentario.isEmpty()) {
            _state.value = _state.value.copy(errorMsg = "Escribe un comentario.")
            return
        }
        if (estrellas == 0) {
            _state.value = _state.value.copy(errorMsg = "Selecciona estrellas.")
            return
        }

        viewModelScope.launch {
            try {
                repo.crearTicket(
                    TicketRequest(
                        idUsuario = idUsuario,
                        idProducto = idProducto,
                        clasificacion = estrellas,
                        comentario = comentario
                    )
                )

                // Reiniciar formulario
                _state.value = _state.value.copy(
                    comentario = "",
                    calificacion = 0,
                    successMsg = "¡Gracias por tu reseña!"
                )

                loadTickets(idProducto)

            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMsg = "Error al enviar reseña.")
            }
        }
    }
}

// -------------------------------------------------------------
// FACTORY INCLUIDA EN ESTE MISMO ARCHIVO
// -------------------------------------------------------------
class TicketViewModelFactory(
    private val repo: TicketRemoteRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TicketViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TicketViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}
