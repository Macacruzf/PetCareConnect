package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.petcareconnect.data.remote.dto.*
import com.example.petcareconnect.data.remote.repository.TicketRemoteRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// -------------------------------------------------------------
// ESTADO UI COMPLETO (CLIENTE + ADMIN)
// -------------------------------------------------------------
data class TicketUiState(
    val tickets: List<TicketResponse> = emptyList(),
    val comentarios: List<ComentarioResponse> = emptyList(),

    val comentario: String = "",        // comentario cliente
    val calificacion: Int = 0,          // estrellas cliente
    val nuevoComentario: String = "",   // respuesta admin

    val successMsg: String? = null,
    val errorMsg: String? = null
)

// -------------------------------------------------------------
// VIEWMODEL
// -------------------------------------------------------------
class TicketViewModel(
    private val repo: TicketRemoteRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TicketUiState())
    val state: StateFlow<TicketUiState> = _state

    // Reset para evitar mezclar productos
    fun resetState() {
        _state.value = TicketUiState()
    }

    // ---------------------------------------------------------
    // Cliente: escribir reseña
    // ---------------------------------------------------------
    fun onComentarioChange(value: String) {
        _state.value = _state.value.copy(comentario = value)
    }

    fun onCalificacionChange(value: Int) {
        _state.value = _state.value.copy(calificacion = value)
    }

    // ---------------------------------------------------------
    // Admin: escribir respuesta
    // ---------------------------------------------------------
    fun onNuevoComentarioChange(value: String) {
        _state.value = _state.value.copy(nuevoComentario = value)
    }

    // ---------------------------------------------------------
    // Cargar reseñas por producto
    // ---------------------------------------------------------
    fun loadTickets(idProducto: Long) {
        viewModelScope.launch {
            try {
                val lista = repo.listarPorProducto(idProducto)
                _state.value = _state.value.copy(
                    tickets = lista,
                    errorMsg = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMsg = "Error al cargar reseñas.")
                autoClearMessage()
            }
        }
    }

    // ---------------------------------------------------------
    // Cargar comentarios de un ticket
    // ---------------------------------------------------------
    fun loadComentarios(idTicket: Long) {
        viewModelScope.launch {
            try {
                val lista = repo.obtenerComentarios(idTicket)
                _state.value = _state.value.copy(
                    comentarios = lista,
                    errorMsg = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMsg = "Error al cargar comentarios.")
                autoClearMessage()
            }
        }
    }

    // ---------------------------------------------------------
    // CREAR RESEÑA (CLIENTE)
    // ---------------------------------------------------------
    fun enviarTicket(idProducto: Long, idUsuario: Long) {
        val comentario = state.value.comentario.trim()
        val estrellas = state.value.calificacion

        if (comentario.isEmpty()) {
            _state.value = _state.value.copy(errorMsg = "Escribe un comentario.")
            autoClearMessage()
            return
        }
        if (estrellas == 0) {
            _state.value = _state.value.copy(errorMsg = "Selecciona estrellas.")
            autoClearMessage()
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

                _state.value = _state.value.copy(
                    comentario = "",
                    calificacion = 0,
                    successMsg = "¡Gracias por tu reseña!",
                    errorMsg = null
                )

                loadTickets(idProducto)
                autoClearMessage()

            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMsg = "Error al enviar reseña.")
                autoClearMessage()
            }
        }
    }

    // ---------------------------------------------------------
    // AGREGAR RESPUESTA DEL ADMIN
    // ---------------------------------------------------------
    fun agregarComentario(idTicket: Long, idUsuario: Long) {

        val mensaje = state.value.nuevoComentario.trim()

        if (mensaje.isEmpty()) {
            _state.value = _state.value.copy(errorMsg = "Escribe una respuesta.")
            autoClearMessage()
            return
        }

        viewModelScope.launch {
            try {
                repo.agregarComentario(
                    idTicket,
                    ComentarioRequest(
                        idUsuario = idUsuario,
                        mensaje = mensaje,
                        tipoMensaje = "SOPORTE"
                    )
                )

                _state.value = _state.value.copy(
                    nuevoComentario = "",
                    successMsg = "Respuesta enviada.",
                    errorMsg = null
                )

                // 🔹 Recargar comentarios
                loadComentarios(idTicket)

                // 🔹 Obtener idProducto desde el ticket ya cargado
                val productoId = _state.value.tickets
                    .firstOrNull { it.idTicket == idTicket }
                    ?.idProducto

                if (productoId != null) {
                    loadTickets(productoId)
                }

                autoClearMessage()

            } catch (e: Exception) {
                _state.value = _state.value.copy(errorMsg = "Error al enviar respuesta.")
                autoClearMessage()
            }
        }
    }

    // ---------------------------------------------------------
    // Mensajes temporales
    // ---------------------------------------------------------
    private fun autoClearMessage() {
        viewModelScope.launch {
            delay(2000)
            _state.value = _state.value.copy(
                successMsg = null,
                errorMsg = null
            )
        }
    }
}

// -------------------------------------------------------------
// FACTORY
// -------------------------------------------------------------
class TicketViewModelFactory(
    private val repo: TicketRemoteRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TicketViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TicketViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
