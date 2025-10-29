package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.petcareconnect.data.model.Carrito

// ---------------------------------------------------------------------------
// CarritoViewModel.kt
// ---------------------------------------------------------------------------
// Este ViewModel gestiona la lógica del carrito de compras de PetCare Connect.
// Utiliza un flujo de estado (StateFlow) para reflejar los cambios en la interfaz
// en tiempo real, como agregar, eliminar o actualizar productos dentro del carrito.
// ---------------------------------------------------------------------------


// ---------------------- ESTADO DE UI ----------------------

// Estado que representa el contenido actual del carrito y el total acumulado.
data class CarritoUiState(
    val items: List<Carrito> = emptyList(), // Lista de productos en el carrito
    val total: Double = 0.0                 // Total calculado de la compra
)


// ---------------------- VIEWMODEL PRINCIPAL ----------------------
class CarritoViewModel : ViewModel() {

    // Flujo de estado que expone los datos del carrito a la interfaz.
    private val _state = MutableStateFlow(CarritoUiState())
    val state: StateFlow<CarritoUiState> = _state


    // ---------------------- AGREGAR ITEM ----------------------
    // Agrega un producto al carrito. Si el producto ya existe, incrementa su cantidad.
    // Este método produce una animación visual reactiva en la UI,
    // ya que Compose vuelve a renderizar la lista automáticamente con el nuevo estado.
    fun agregarItem(item: Carrito) {
        _state.update { current ->

            // Verifica si el producto ya existe en la lista
            val existente = current.items.find { it.idProducto == item.idProducto }

            // Si el producto existe, aumenta su cantidad; si no, lo agrega nuevo
            val nuevosItems = if (existente != null) {
                current.items.map {
                    if (it.idProducto == item.idProducto)
                        it.copy(cantidad = it.cantidad + item.cantidad)
                    else it
                }
            } else {
                current.items + item
            }

            // Retorna el nuevo estado con los cambios actualizados
            current.copy(
                items = nuevosItems,
                total = nuevosItems.sumOf { it.precio * it.cantidad } // recalcula el total
            )
        }
    }


    // ---------------------- ELIMINAR ITEM ----------------------
    // Elimina un producto específico del carrito según su identificador.
    // Al eliminarlo, la interfaz se actualiza de forma inmediata gracias a Compose.
    fun eliminarItem(idItem: Int) {
        _state.update { current ->
            val nuevosItems = current.items.filterNot { it.idItem == idItem }

            current.copy(
                items = nuevosItems,
                total = nuevosItems.sumOf { it.precio * it.cantidad }
            )
        }
    }


    // ---------------------- ACTUALIZAR CANTIDAD ----------------------
    // Permite modificar manualmente la cantidad de un producto dentro del carrito.
    // Cada cambio reactiva la recomposición en pantalla mostrando el nuevo total.
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


    // ---------------------- VACIAR CARRITO ----------------------
    // Limpia por completo el contenido del carrito.
    // Este método suele activarse al finalizar una compra o cancelar un pedido.
    fun vaciarCarrito() {
        _state.value = CarritoUiState()
    }
}
