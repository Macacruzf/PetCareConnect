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

    val imagenResId: Int? = null,
    val imagenUri: String? = null,

    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    val successMsg: String? = null,

    val productoEnEdicionId: Int? = null
)

// ----------------------------------------------------------
// VIEWMODEL
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
    private fun loadProductos() {
        viewModelScope.launch {
            repository.getAllProductos().collect { productos ->
                _state.value = _state.value.copy(productos = productos)
            }
        }
    }

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
    // AGREGAR PRODUCTO
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
                    imagenResId = _state.value.imagenResId,
                    imagenUri = _state.value.imagenUri
                )
            )
            limpiarFormulario("Producto agregado correctamente.")
        }
    }

    // ------------------------------------------------------
    // CARGAR DATOS PARA EDITAR
    // ------------------------------------------------------
    fun cargarProductoParaEdicion(producto: Producto) {
        _state.value = _state.value.copy(
            productoEnEdicionId = producto.idProducto,
            nombre = producto.nombre,
            precio = producto.precio.toString(),
            stock = producto.stock.toString(),
            categoriaId = producto.categoriaId,
            estado = producto.estado,
            imagenResId = producto.imagenResId,
            imagenUri = producto.imagenUri
        )
    }

    // ------------------------------------------------------
    // EDITAR PRODUCTO
    // ------------------------------------------------------
    fun editarProducto() {
        val id = _state.value.productoEnEdicionId
            ?: return setError("No se pudo identificar el producto.")

        val nombre = _state.value.nombre.trim()
        val precio = _state.value.precio.toDoubleOrNull()
        val stock = _state.value.stock.toIntOrNull()
        val categoriaId = _state.value.categoriaId

        if (nombre.isBlank()) return setError("El nombre es obligatorio.")
        if (precio == null || precio <= 0) return setError("El precio es inválido.")
        if (stock == null || stock < 0) return setError("El stock es inválido.")
        if (categoriaId == null) return setError("Selecciona una categoría.")

        viewModelScope.launch {
            val productoActual = _state.value.productos.find { it.idProducto == id }
                ?: return@launch setError("Producto no encontrado.")

            repository.update(
                productoActual.copy(
                    nombre = nombre,
                    precio = precio,
                    stock = stock,
                    categoriaId = categoriaId,
                    estado = _state.value.estado,
                    imagenResId = _state.value.imagenResId,
                    imagenUri = _state.value.imagenUri
                )
            )

            limpiarFormulario("Producto actualizado correctamente.")
        }
    }

    // ------------------------------------------------------
    fun deleteProducto(idProducto: Int) {
        viewModelScope.launch {
            repository.deleteById(idProducto)
            setSuccess("Producto eliminado correctamente.")
        }
    }

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

            if (nuevoStock == 0)
                repository.updateEstado(idProducto, EstadoProducto.SIN_STOCK)

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
    fun onNombreChange(v: String) = update { copy(nombre = v) }
    fun onPrecioChange(v: String) = update { copy(precio = v) }
    fun onStockChange(v: String) = update { copy(stock = v) }
    fun onCategoriaChange(v: Int) = update { copy(categoriaId = v) }
    fun onEstadoChange(v: EstadoProducto) = update { copy(estado = v) }

    fun onImagenChange(v: Int?) = update { copy(imagenResId = v, imagenUri = null) }
    fun onImagenUriChange(uri: String?) = update { copy(imagenUri = uri, imagenResId = null) }

    private fun update(reducer: ProductoUiState.() -> ProductoUiState) {
        _state.value = _state.value.reducer()
    }

    // ------------------------------------------------------
    // LIMPIEZA DE FORMULARIOS
    // ------------------------------------------------------
    private fun limpiarFormulario(msg: String) {
        _state.value = _state.value.copy(
            successMsg = msg,
            errorMsg = null,

            nombre = "",
            precio = "",
            stock = "",
            categoriaId = null,
            estado = EstadoProducto.DISPONIBLE,

            imagenResId = null,
            imagenUri = null,

            productoEnEdicionId = null
        )
    }

    // ⭐ Nueva función solicitada
    fun resetFormulario() {
        _state.value = _state.value.copy(
            nombre = "",
            precio = "",
            stock = "",
            categoriaId = null,
            estado = EstadoProducto.DISPONIBLE,
            imagenResId = null,
            imagenUri = null,
            productoEnEdicionId = null,
            errorMsg = null,
            successMsg = null
        )
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
