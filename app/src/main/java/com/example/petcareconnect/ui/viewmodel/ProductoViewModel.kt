package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.model.Categoria
import com.example.petcareconnect.data.model.EstadoProducto
import com.example.petcareconnect.data.remote.ProductoRemoteRepository
import com.example.petcareconnect.data.remote.dto.CategoriaSimpleDto
import com.example.petcareconnect.data.remote.dto.EstadoRequest
import com.example.petcareconnect.data.remote.dto.ProductoUpdateRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ----------------------------------------------------------
// UI STATE
// ----------------------------------------------------------
data class ProductoUiState(
    val productos: List<Producto> = emptyList(),
    val categorias: List<Categoria> = emptyList(),

    // formulario
    val nombre: String = "",
    val precio: String = "",
    val stock: String = "",
    val categoriaId: Long? = null,
    val estado: EstadoProducto = EstadoProducto.DISPONIBLE,
    val imagenResId: Int? = null,
    val imagenUri: String? = null,

    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    val successMsg: String? = null,

    val productoEnEdicionId: Long? = null
)

// ----------------------------------------------------------
// VIEWMODEL → SOLO BACKEND
// ----------------------------------------------------------
class ProductoViewModel(
    private val remoteRepository: ProductoRemoteRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProductoUiState())
    val state: StateFlow<ProductoUiState> = _state

    init {
        loadProductos()
        loadCategorias()
    }

    // ------------------------------------------------------
    // CARGAR PRODUCTOS DESDE BACKEND
    // ------------------------------------------------------
    private fun loadProductos() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)

                val productos = remoteRepository.getAllProductosRemotos()
                _state.value = _state.value.copy(
                    productos = productos,
                    isLoading = false
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMsg = "Error cargando productos: ${e.message}"
                )
            }
        }
    }

    // ------------------------------------------------------
    // CATEGORÍAS (MOMENTÁNEO)
    // ------------------------------------------------------
    private fun loadCategorias() {
        _state.value = _state.value.copy(
            categorias = listOf(
                Categoria(1, "Alimentos"),
                Categoria(2, "Accesorios"),
                Categoria(3, "Farmacia"),
                Categoria(4, "Juguetes")
            )
        )
    }

    // ------------------------------------------------------
    // INSERTAR PRODUCTO
    // ------------------------------------------------------
    fun insertProducto() {
        val nombre = state.value.nombre.trim()
        val precio = state.value.precio.toDoubleOrNull()
        val stock = state.value.stock.toIntOrNull()
        val categoriaId = state.value.categoriaId

        if (nombre.isBlank()) return setError("El nombre es obligatorio.")
        if (precio == null) return setError("Precio inválido.")
        if (stock == null) return setError("Stock inválido.")
        if (categoriaId == null) return setError("Selecciona una categoría.")

        viewModelScope.launch {
            try {
                remoteRepository.crearProductoRemoto(
                    nombre = nombre,
                    precio = precio,
                    stock = stock,
                    categoriaId = categoriaId
                )

                setSuccess("Producto creado correctamente")
                loadProductos()
                limpiarFormulario()

            } catch (e: Exception) {
                setError("Error al crear producto: ${e.message}")
            }
        }
    }

    // ------------------------------------------------------
    // CARGAR PRODUCTO PARA EDITAR
    // ------------------------------------------------------
    fun cargarProductoParaEdicion(producto: Producto) {
        _state.value = _state.value.copy(
            productoEnEdicionId = producto.idProducto,
            nombre = producto.nombre,
            precio = producto.precio.toString(),
            stock = producto.stock.toString(),
            categoriaId = producto.categoriaId,
            estado = producto.estado,
            imagenUri = producto.imagenUri,
            imagenResId = producto.imagenResId
        )
    }

    // ------------------------------------------------------
    // EDITAR PRODUCTO
    // ------------------------------------------------------
    fun editarProducto() {
        val id = state.value.productoEnEdicionId
            ?: return setError("Producto no identificado")

        val nombre = state.value.nombre.trim()
        val precio = state.value.precio.toDoubleOrNull()
        val stock = state.value.stock.toIntOrNull()
        val categoriaId = state.value.categoriaId
        val estado = state.value.estado

        if (nombre.isBlank()) return setError("Nombre obligatorio")
        if (precio == null) return setError("Precio inválido")
        if (stock == null) return setError("Stock inválido")
        if (categoriaId == null) return setError("Selecciona categoría")

        val request = ProductoUpdateRequest(
            nombre = nombre,
            precio = precio,
            stock = stock,
            estado = estado.name,
            categoria = CategoriaSimpleDto(categoriaId, "")
        )

        viewModelScope.launch {
            try {
                remoteRepository.actualizarProductoRemoto(id, request)

                setSuccess("Producto actualizado")
                loadProductos()       // ← recarga UI cliente + admin
                limpiarFormulario()

            } catch (e: Exception) {
                setError("Error al actualizar: ${e.message}")
            }
        }
    }

    // ------------------------------------------------------
    // ELIMINAR PRODUCTO
    // ------------------------------------------------------
    fun deleteProducto(id: Long) {
        viewModelScope.launch {
            try {
                remoteRepository.eliminarProductoRemoto(id)
                setSuccess("Producto eliminado")
                loadProductos()

            } catch (e: Exception) {
                setError("Error al eliminar: ${e.message}")
            }
        }
    }

    // ------------------------------------------------------
    // CAMBIAR ESTADO
    // ------------------------------------------------------
    fun cambiarEstadoManual(idProducto: Long, nuevoEstado: EstadoProducto) {
        viewModelScope.launch {
            try {
                remoteRepository.cambiarEstadoRemoto(
                    idProducto,
                    EstadoRequest(nuevoEstado.name)
                )

                setSuccess("Estado actualizado")
                loadProductos()   // ← ACTUALIZA TODO EN VIVO

            } catch (e: Exception) {
                setError("Error al cambiar estado: ${e.message}")
            }
        }
    }

    // ------------------------------------------------------
    // FORMULARIO
    // ------------------------------------------------------
    fun onNombreChange(v: String) = update { copy(nombre = v) }
    fun onPrecioChange(v: String) = update { copy(precio = v) }
    fun onStockChange(v: String) = update { copy(stock = v) }
    fun onCategoriaChange(v: Long) = update { copy(categoriaId = v) }
    fun onEstadoChange(v: EstadoProducto) = update { copy(estado = v) }
    fun onImagenChange(id: Int?) = update { copy(imagenResId = id, imagenUri = null) }
    fun onImagenUriChange(uri: String?) = update { copy(imagenUri = uri, imagenResId = null) }

    private fun update(block: ProductoUiState.() -> ProductoUiState) {
        _state.value = _state.value.block()
    }

    private fun limpiarFormulario() {
        _state.value = _state.value.copy(
            nombre = "",
            precio = "",
            stock = "",
            categoriaId = null,
            estado = EstadoProducto.DISPONIBLE,
            imagenUri = null,
            imagenResId = null,
            productoEnEdicionId = null
        )
    }

    private fun setError(msg: String) {
        _state.value = _state.value.copy(errorMsg = msg, successMsg = null)
    }

    private fun setSuccess(msg: String) {
        _state.value = _state.value.copy(successMsg = msg, errorMsg = null)
    }

    fun limpiarMensajes() {
        _state.value = _state.value.copy(
            successMsg = null,
            errorMsg = null
        )
    }
}

// ----------------------------------------------------------
// FACTORY
// ----------------------------------------------------------
class ProductoViewModelFactory(
    private val remoteRepository: ProductoRemoteRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductoViewModel(remoteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
