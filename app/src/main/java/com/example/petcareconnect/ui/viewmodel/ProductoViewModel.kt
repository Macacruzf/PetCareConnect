package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.model.Categoria
import com.example.petcareconnect.data.model.EstadoProducto
import com.example.petcareconnect.data.repository.ProductoRepository
import com.example.petcareconnect.data.repository.CategoriaRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ----------------------------------------------------------
// UI STATE
// ----------------------------------------------------------
data class ProductoUiState(
    val productos: List<Producto> = emptyList(),
    val categorias: List<Categoria> = emptyList(),

    val nombre: String = "",
    val precio: String = "",
    val stock: String = "",
    val categoriaId: Int? = null,

    val estado: EstadoProducto = EstadoProducto.DISPONIBLE,

    val imagenResId: Int? = null,   // drawable
    val imagenUri: String? = null,   // cámara o galería

    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    val successMsg: String? = null
)

// ----------------------------------------------------------
// VIEW MODEL
// ----------------------------------------------------------
class ProductoViewModel(
    private val repository: ProductoRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProductoUiState())
    val state: StateFlow<ProductoUiState> = _state

    init {
        loadProductos()
        loadCategorias()
    }

    // ------------------------------------------------------
    // CARGAR PRODUCTOS
    // ------------------------------------------------------
    private fun loadProductos() {
        viewModelScope.launch {
            repository.getAllProductos().collect { productos ->
                _state.value = _state.value.copy(productos = productos)
            }
        }
    }

    // ------------------------------------------------------
    // CARGAR CATEGORÍAS
    // ------------------------------------------------------
    private fun loadCategorias() {
        viewModelScope.launch {
            categoriaRepository.getAllCategorias().collect { categorias ->
                if (categorias.isEmpty()) {
                    delay(500)
                    val backup = categoriaRepository.getAllOnce()
                    _state.value = _state.value.copy(categorias = backup)
                } else {
                    _state.value = _state.value.copy(categorias = categorias)
                }
            }
        }
    }

    fun recargarCategoriasManualmente() {
        viewModelScope.launch {
            val categorias = categoriaRepository.getAllOnce()
            _state.value = _state.value.copy(categorias = categorias)
        }
    }

    // ------------------------------------------------------
    // INSERTAR PRODUCTO
    // ------------------------------------------------------
    fun insertProducto() {
        val nombre = _state.value.nombre.trim()
        val precio = _state.value.precio.toDoubleOrNull()
        val stock = _state.value.stock.toIntOrNull()
        val categoriaId = _state.value.categoriaId

        if (nombre.isBlank()) return setError("El nombre es obligatorio.")
        if (precio == null || precio <= 0) return setError("El precio es inválido.")
        if (stock == null || stock < 0) return setError("El stock es inválido.")
        if (categoriaId == null) return setError("Selecciona una categoría.")

        viewModelScope.launch {
            repository.insert(
                Producto(
                    nombre = nombre,
                    precio = precio,
                    stock = stock,
                    categoriaId = categoriaId,
                    estado = _state.value.estado,

                    imagenResId = _state.value.imagenResId,  // drawable
                    imagenUri = _state.value.imagenUri       // cámara/galería
                )
            )

            _state.value = _state.value.copy(
                successMsg = "Producto agregado correctamente.",
                nombre = "",
                precio = "",
                stock = "",
                categoriaId = null,
                estado = EstadoProducto.DISPONIBLE,
                imagenResId = null,
                imagenUri = null
            )
        }
    }

    // ------------------------------------------------------
    // ELIMINAR PRODUCTO
    // ------------------------------------------------------
    fun deleteProducto(idProducto: Int) {
        viewModelScope.launch {
            repository.deleteById(idProducto)
            setSuccess("Producto eliminado correctamente.")
        }
    }

    // ------------------------------------------------------
    // CAMBIAR ESTADO MANUAL
    // ------------------------------------------------------
    fun cambiarEstadoManual(idProducto: Int, nuevoEstado: EstadoProducto) {
        viewModelScope.launch {
            repository.updateEstado(idProducto, nuevoEstado)
            setSuccess("Estado actualizado a ${nuevoEstado.name}.")
        }
    }

    // ------------------------------------------------------
    // STOCK
    // ------------------------------------------------------
    fun descontarStock(idProducto: Int, cantidad: Int) {
        viewModelScope.launch {
            val producto = state.value.productos.find { it.idProducto == idProducto }
                ?: return@launch setError("Producto no encontrado.")

            val nuevoStock = producto.stock - cantidad
            if (nuevoStock < 0) return@launch setError("Stock insuficiente.")

            repository.updateStock(idProducto, nuevoStock)

            if (nuevoStock == 0) repository.updateEstado(idProducto, EstadoProducto.SIN_STOCK)
            setSuccess("Stock actualizado correctamente.")
        }
    }

    fun registrarCompra(idProducto: Int, cantidad: Int) {
        viewModelScope.launch {
            val producto = state.value.productos.find { it.idProducto == idProducto }
                ?: return@launch setError("Producto no encontrado.")

            val nuevoStock = producto.stock + cantidad
            repository.updateStock(idProducto, nuevoStock)

            if (producto.estado == EstadoProducto.SIN_STOCK && nuevoStock > 0) {
                repository.updateEstado(idProducto, EstadoProducto.DISPONIBLE)
            }

            setSuccess("Stock ingresado correctamente.")
        }
    }

    // ------------------------------------------------------
    // SETTERS
    // ------------------------------------------------------
    fun onNombreChange(v: String) { _state.value = _state.value.copy(nombre = v) }

    fun onPrecioChange(v: String) { _state.value = _state.value.copy(precio = v) }

    fun onStockChange(v: String) { _state.value = _state.value.copy(stock = v) }

    fun onCategoriaChange(v: Int) { _state.value = _state.value.copy(categoriaId = v) }

    fun onEstadoChange(v: EstadoProducto) { _state.value = _state.value.copy(estado = v) }

    fun onImagenChange(v: Int?) { _state.value = _state.value.copy(imagenResId = v, imagenUri = null) }

    fun onImagenUriChange(uri: String?) {
        _state.value = _state.value.copy(imagenUri = uri, imagenResId = null)
    }

    // ------------------------------------------------------
    // UTILIDADES
    // ------------------------------------------------------
    private fun setError(msg: String) {
        _state.value = _state.value.copy(errorMsg = msg, successMsg = null)
    }

    private fun setSuccess(msg: String) {
        _state.value = _state.value.copy(successMsg = msg, errorMsg = null)
    }

    fun limpiarMensajes() {
        _state.value = _state.value.copy(errorMsg = null, successMsg = null)
    }
}

// ----------------------------------------------------------
// FACTORY
// ----------------------------------------------------------
class ProductoViewModelFactory(
    private val repository: ProductoRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductoViewModel(repository, categoriaRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
