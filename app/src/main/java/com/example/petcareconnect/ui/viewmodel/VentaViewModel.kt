package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.petcareconnect.data.model.Venta
import com.example.petcareconnect.data.model.DetalleVenta
import com.example.petcareconnect.data.repository.VentaRepository
import com.example.petcareconnect.data.repository.DetalleVentaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ---------------------------------------------------
// 🔹 Estado UI de la pantalla de ventas
// ---------------------------------------------------
data class VentaUiState(
    val ventas: List<Venta> = emptyList(),         // Lista de ventas registradas
    val detalles: List<DetalleVenta> = emptyList(),// Detalles (carrito temporal)
    val total: Double = 0.0,                       // Total acumulado
    val cliente: String = "",                      // Nombre del cliente
    val isSubmitting: Boolean = false,             // Indicador de carga
    val successMsg: String? = null,                // Mensaje de éxito
    val errorMsg: String? = null                   // Mensaje de error
)

// ---------------------------------------------------
// 🔹 ViewModel principal de ventas
// ---------------------------------------------------
class VentaViewModel(
    private val ventaRepo: VentaRepository,
    private val detalleRepo: DetalleVentaRepository
) : ViewModel() {

    private val _state = MutableStateFlow(VentaUiState())
    val state: StateFlow<VentaUiState> = _state

    init {
        loadVentas()
    }

    // ---------------------------------------------------
    // Cargar todas las ventas registradas
    // ---------------------------------------------------
    private fun loadVentas() {
        viewModelScope.launch {
            ventaRepo.getAllVentas().collect { lista ->
                _state.value = _state.value.copy(ventas = lista)
            }
        }
    }

    // ---------------------------------------------------
    // Agregar un producto al detalle (carrito)
    // ---------------------------------------------------
    fun agregarDetalle(productoId: Int, nombre: String, precio: Double, cantidad: Int) {
        val nuevoDetalle = DetalleVenta(
            idDetalle = 0,
            ventaId = 0, // aún no se guarda la venta
            productoId = productoId,
            nombre = nombre,
            cantidad = cantidad,
            subtotal = precio * cantidad
        )

        val nuevosDetalles = _state.value.detalles + nuevoDetalle
        val nuevoTotal = nuevosDetalles.sumOf { it.subtotal }

        _state.value = _state.value.copy(detalles = nuevosDetalles, total = nuevoTotal)
    }

    // ---------------------------------------------------
    // Eliminar un producto del detalle
    // ---------------------------------------------------
    fun eliminarDetalle(index: Int) {
        val nuevos = _state.value.detalles.toMutableList().also {
            if (index in it.indices) it.removeAt(index)
        }
        val nuevoTotal = nuevos.sumOf { it.subtotal }
        _state.value = _state.value.copy(detalles = nuevos, total = nuevoTotal)
    }

    // ---------------------------------------------------
    // Guardar la venta completa con sus detalles
    // ---------------------------------------------------
    fun guardarVenta() {
        val cliente = _state.value.cliente
        val detalles = _state.value.detalles

        if (cliente.isBlank()) {
            _state.value = _state.value.copy(errorMsg = "Debe ingresar el nombre del cliente.")
            return
        }

        if (detalles.isEmpty()) {
            _state.value = _state.value.copy(errorMsg = "Debe agregar al menos un producto.")
            return
        }

        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isSubmitting = true, errorMsg = null, successMsg = null)

                // 1️⃣ Guardar la venta principal
                val venta = Venta(
                    idVenta = 0,
                    fecha = System.currentTimeMillis(), // guardamos timestamp
                    cliente = cliente,
                    total = _state.value.total
                )
                val idVenta = ventaRepo.insert(venta)

                // 2️⃣ Guardar los detalles asociados
                _state.value.detalles.forEach {
                    detalleRepo.insert(it.copy(ventaId = idVenta.toInt()))
                }

                // 3️⃣ Limpiar estado
                _state.value = _state.value.copy(
                    detalles = emptyList(),
                    total = 0.0,
                    cliente = "",
                    isSubmitting = false,
                    successMsg = "Venta registrada correctamente"
                )
                loadVentas()

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSubmitting = false,
                    errorMsg = "Error al registrar la venta: ${e.message}"
                )
            }
        }
    }

    // ---------------------------------------------------
    // Actualizar el campo cliente
    // ---------------------------------------------------
    fun onClienteChange(value: String) {
        _state.value = _state.value.copy(cliente = value)
    }

    // ---------------------------------------------------
    // Limpiar mensajes de error o éxito
    // ---------------------------------------------------
    fun clearMessages() {
        _state.value = _state.value.copy(successMsg = null, errorMsg = null)
    }
}

// ---------------------------------------------------
// 🔹 Factory para inyección de dependencias
// ---------------------------------------------------
class VentaViewModelFactory(
    private val ventaRepo: VentaRepository,
    private val detalleRepo: DetalleVentaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VentaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VentaViewModel(ventaRepo, detalleRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
