package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.petcareconnect.data.model.Carrito

data class CarritoUiState(
    val items: List<Carrito> = emptyList(),
    val total: Double = 0.0
)

class CarritoViewModel : ViewModel() {

    private val _state = MutableStateFlow(CarritoUiState())
    val state: StateFlow<CarritoUiState> = _state

    // 🔹 Agregar producto al carrito
    fun agregarItem(item: Carrito) {
        _state.update { current ->
            val existente = current.items.find { it.idItem == item.idItem }
            val nuevosItems = if (existente != null) {
                current.items.map {
                    if (it.idItem == item.idItem)
                        it.copy(cantidad = it.cantidad + item.cantidad)
                    else it
                }
            } else {
                current.items + item
            }

            current.copy(
                items = nuevosItems,
                total = nuevosItems.sumOf { it.precio * it.cantidad }
            )
        }
    }

    // 🔹 Eliminar producto
    fun eliminarItem(idItem: Int) {
        _state.update { current ->
            val nuevosItems = current.items.filterNot { it.idItem == idItem }
            current.copy(
                items = nuevosItems,
                total = nuevosItems.sumOf { it.precio * it.cantidad }
            )
        }
    }

    // 🔹 Actualizar cantidad
    fun actualizarCantidad(item: Carrito, nuevaCantidad: Int) {
        _state.update { current ->
            val nuevosItems = current.items.map {
                if (it.idItem == item.idItem) it.copy(cantidad = nuevaCantidad) else it
            }
            current.copy(
                items = nuevosItems,
                total = nuevosItems.sumOf { it.precio * it.cantidad }
            )
        }
    }

    // 🔹 Vaciar carrito
    fun vaciarCarrito() {
        _state.value = CarritoUiState()
    }
}
