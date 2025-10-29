package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petcareconnect.data.model.Ticket
import com.example.petcareconnect.data.repository.TicketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/*
 * ---------------------------------------------------------------------------
 * TicketViewModel.kt
 * ---------------------------------------------------------------------------
 * Este ViewModel controla toda la lógica relacionada con los comentarios y
 * calificaciones (reseñas) de productos dentro de la aplicación PetCare Connect.
 *
 * Administra:
 *  - La carga de tickets (comentarios) desde la base de datos.
 *  - El envío de nuevas reseñas.
 *  - La actualización reactiva del estado que observa la interfaz Compose.
 *
 * Cada cambio en el estado provoca una recomposición automática en la UI,
 * lo que genera una animación de actualización fluida sin necesidad de código
 * visual explícito (gracias al motor de recomposición de Jetpack Compose).
 * ---------------------------------------------------------------------------
 */


// ---------------------------------------------------------------------------
// ESTADO DE UI (TicketUiState)
// ---------------------------------------------------------------------------
// Contiene los datos visibles y editables desde la interfaz de reseñas:
// lista de tickets, texto del comentario, calificación y mensajes de estado.
// Los cambios en este estado re-renderizan automáticamente la interfaz.
data class TicketUiState(
    val tickets: List<Ticket> = emptyList(),
    val comentario: String = "",
    val calificacion: Int = 0,
    val successMsg: String? = null,
    val errorMsg: String? = null
)


// ---------------------------------------------------------------------------
// VIEWMODEL
// ---------------------------------------------------------------------------
// Controla la comunicación entre la UI y el repositorio de tickets.
// Administra los flujos de datos y las operaciones de escritura/lectura.
class TicketViewModel(private val repo: TicketRepository) : ViewModel() {

    // Estado observable de la UI.
    private val _state = MutableStateFlow(TicketUiState())
    val state: StateFlow<TicketUiState> = _state


    // -----------------------------------------------------------------------
    // CARGAR TICKETS DE UN PRODUCTO
    // -----------------------------------------------------------------------
    // Recupera de forma reactiva todas las reseñas asociadas a un producto.
    // Cuando el flujo emite nuevos valores, la lista de la UI se actualiza
    // automáticamente, con animaciones suaves en elementos tipo LazyColumn.
    fun loadTickets(idProducto: Int) {
        viewModelScope.launch {
            repo.getTicketsByProducto(idProducto).collect { list ->
                _state.value = _state.value.copy(tickets = list)
            }
        }
    }


    // -----------------------------------------------------------------------
    // ACTUALIZAR COMENTARIO
    // -----------------------------------------------------------------------
    // Cada vez que el usuario escribe en el campo de texto, se actualiza
    // el valor local y la UI se recompone instantáneamente, mostrando el
    // texto en tiempo real con animación interna de Compose.
    fun onComentarioChange(value: String) {
        _state.value = _state.value.copy(comentario = value)
    }


    // -----------------------------------------------------------------------
    // ACTUALIZAR CALIFICACIÓN
    // -----------------------------------------------------------------------
    // Al seleccionar una estrella, este método modifica el valor de la
    // calificación. Compose vuelve a dibujar las estrellas, resaltando las
    // seleccionadas mediante una animación de cambio de color y opacidad.
    fun onCalificacionChange(value: Int) {
        _state.value = _state.value.copy(calificacion = value)
    }


    // -----------------------------------------------------------------------
    // ENVIAR NUEVA RESEÑA
    // -----------------------------------------------------------------------
    // Inserta un nuevo comentario y calificación en la base de datos.
    // Si los campos no están completos, se muestra un mensaje de error.
    // Una vez enviada la reseña, se limpian los campos y Compose actualiza
    // de manera reactiva la lista, mostrando la nueva entrada con una
    // transición visual fluida dentro del listado de reseñas.
    fun enviarTicket(idProducto: Int, usuario: String) {
        val c = _state.value.comentario.trim()
        val calif = _state.value.calificacion

        // Validación simple: ambos campos deben estar completos.
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

            // Actualiza el estado para limpiar el formulario y mostrar mensaje.
            _state.value = _state.value.copy(
                comentario = "",
                calificacion = 0,
                successMsg = "¡Gracias por tu reseña!"
            )
        }
    }
}
