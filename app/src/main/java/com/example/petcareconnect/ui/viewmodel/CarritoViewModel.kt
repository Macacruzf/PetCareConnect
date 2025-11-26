package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.petcareconnect.data.model.Carrito
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

// -----------------------------------------------------------
//  ESTADO DE UI DEL CARRITO
// -----------------------------------------------------------
data class CarritoUiState(
    val items: List<Carrito> = emptyList(),
    val total: Double = 0.0
)

// -----------------------------------------------------------
//  VIEWMODEL PRINCIPAL DEL CARRITO
// -----------------------------------------------------------
class CarritoViewModel : ViewModel() {

    private val _state = MutableStateFlow(CarritoUiState())
    val state: StateFlow<CarritoUiState> = _state

    // -----------------------------------------------------------
    //  ⭐ VARIABLES TEMPORALES PARA DETALLE DE VENTA
    // -----------------------------------------------------------
    var compraItemsTemp: List<Carrito> = emptyList()
    var compraTotalTemp: Double = 0.0
    var compraMetodoTemp: String = ""
    // -----------------------------------------------------------

    // -----------------------------------------------------------
    //  AGREGAR ITEM
    // -----------------------------------------------------------
    fun agregarItem(item: Carrito) {
        _state.update { current ->

            val existente = current.items.find { it.idProducto == item.idProducto }

            val nuevosItems = if (existente != null) {

                if (existente.cantidad >= existente.stock) {
                    return@update current
                }

                current.items.map {
                    if (it.idProducto == item.idProducto)
                        it.copy(cantidad = it.cantidad + 1)
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

    // -----------------------------------------------------------
    //  ELIMINAR ITEM
    // -----------------------------------------------------------
    fun eliminarItem(idItem: Int) {
        _state.update { current ->

            val nuevosItems = current.items.filterNot { it.idItem == idItem }

            current.copy(
                items = nuevosItems,
                total = nuevosItems.sumOf { it.precio * it.cantidad }
            )
        }
    }

    // -----------------------------------------------------------
    //  INCREMENTAR
    // -----------------------------------------------------------
    fun incrementarCantidad(idItem: Int) {
        _state.update { current ->

            val nuevosItems = current.items.map { item ->
                if (item.idItem == idItem) {
                    if (item.cantidad < item.stock)
                        item.copy(cantidad = item.cantidad + 1)
                    else item
                } else item
            }

            current.copy(
                items = nuevosItems,
                total = nuevosItems.sumOf { it.precio * it.cantidad }
            )
        }
    }

    // -----------------------------------------------------------
    //  DECREMENTAR
    // -----------------------------------------------------------
    fun decrementarCantidad(idItem: Int) {
        _state.update { current ->

            val nuevosItems = current.items.map { item ->
                if (item.idItem == idItem) {
                    if (item.cantidad > 1)
                        item.copy(cantidad = item.cantidad - 1)
                    else item
                } else item
            }

            current.copy(
                items = nuevosItems,
                total = nuevosItems.sumOf { it.precio * it.cantidad }
            )
        }
    }

    // -----------------------------------------------------------
    //  ACTUALIZAR CANTIDAD DIRECTA
    // -----------------------------------------------------------
    fun actualizarCantidad(item: Carrito, nuevaCantidad: Int) {

        val cantidadFinal = nuevaCantidad.coerceIn(1, item.stock)

        _state.update { current ->

            val nuevosItems = current.items.map {
                if (it.idItem == item.idItem) it.copy(cantidad = cantidadFinal)
                else it
            }

            current.copy(
                items = nuevosItems,
                total = nuevosItems.sumOf { it.precio * it.cantidad }
            )
        }
    }

    // -----------------------------------------------------------
    //  DESCONTAR STOCK EN BACKEND
    // -----------------------------------------------------------
    suspend fun descontarStockBackend(
        descontar: suspend (idProducto: Long, cantidad: Int) -> Unit
    ) {
        state.value.items.forEach { item ->
            descontar(item.idProducto, item.cantidad)
        }
    }

    // -----------------------------------------------------------
    //  VACIAR CARRITO
    // -----------------------------------------------------------
    fun vaciarCarrito() {
        _state.value = CarritoUiState()
    }
}
