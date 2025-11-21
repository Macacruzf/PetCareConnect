package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.model.Categoria
import com.example.petcareconnect.data.model.EstadoProducto
import com.example.petcareconnect.data.remote.dto.ProductoDto
import com.example.petcareconnect.data.remote.ProductoRemoteRepository
import com.example.petcareconnect.data.remote.dto.CategoriaDto
import com.example.petcareconnect.data.remote.dto.EstadoRequest
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
// VIEWMODEL
// ----------------------------------------------------------
class ProductoViewModel(
    private val repository: ProductoRepository,
    private val categoriaRepository: CategoriaRepository,
    private val remoteRepository: ProductoRemoteRepository?
) : ViewModel() {

    private val _state = MutableStateFlow(ProductoUiState())
    val state: StateFlow<ProductoUiState> = _state

    init {
        loadProductos()
        loadCategorias()
        remoteRepository?.let { syncProductosDesdeApi() }
    }

    // ------------------------------------------------------
    // CARGA DE PRODUCTOS LOCALES
    // ------------------------------------------------------
    private fun loadProductos() {
        viewModelScope.launch {
            repository.getAllProductos().collect { productos ->
                _state.value = _state.value.copy(productos = productos)
            }
        }
    }

    // ------------------------------------------------------
    // CARGA DE CATEGORÍAS
    // ------------------------------------------------------
    private fun loadCategorias() {
        viewModelScope.launch {
            categoriaRepository.getAllCategorias().collect { categorias ->
                if (categorias.isEmpty()) {
                    delay(300)
                    _state.value = _state.value.copy(
                        categorias = categoriaRepository.getAllOnce()
                    )
                } else {
                    _state.value = _state.value.copy(categorias = categorias)
                }
            }
        }
    }

    // ------------------------------------------------------
    // SINCRONIZACIÓN DESDE MICROSERVICIO
    // ------------------------------------------------------
    fun syncProductosDesdeApi() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)

                val productosApi = remoteRepository!!.getAllProductosRemotos()

                repository.deleteAll()
                productosApi.forEach { repository.insert(it) }

                _state.value = _state.value.copy(
                    isLoading = false,
                    successMsg = "Productos sincronizados desde servidor"
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMsg = "Error al sincronizar: ${e.message}"
                )
            }
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
        if (precio == null || precio <= 0) return setError("Precio inválido.")
        if (stock == null || stock < 0) return setError("Stock inválido.")
        if (categoriaId == null) return setError("Selecciona categoría.")

        viewModelScope.launch {
            try {
                val productoApi = remoteRepository?.crearProductoRemoto(
                    nombre = nombre,
                    precio = precio,
                    stock = stock,
                    categoriaId = categoriaId
                )

                if (productoApi != null) {
                    repository.insert(productoApi)
                } else {
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
                }

                limpiarFormulario("Producto agregado correctamente")

            } catch (e: Exception) {
                setError("Error al guardar en API: ${e.message}")
            }
        }
    }

    // ------------------------------------------------------
    // CARGAR PRODUCTO PARA EDICIÓN
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
        val id = _state.value.productoEnEdicionId ?: return setError("Producto no identificado.")
        val nombre = _state.value.nombre.trim()
        val precio = _state.value.precio.toDoubleOrNull()
        val stock = _state.value.stock.toIntOrNull()
        val categoriaId = _state.value.categoriaId
        val estado = _state.value.estado
        val imagenUri = _state.value.imagenUri
        val imagenResId = _state.value.imagenResId

        if (nombre.isBlank()) return setError("El nombre es obligatorio.")
        if (precio == null || precio <= 0) return setError("Precio inválido.")
        if (stock == null || stock < 0) return setError("Stock inválido.")
        if (categoriaId == null) return setError("Selecciona categoría.")

        viewModelScope.launch {
            try {
                val categoriaLocal = categoriaRepository.getById(categoriaId)
                val categoriaDto = categoriaLocal?.let {
                    CategoriaDto(idCategoria = it.idCategoria, nombre = it.nombre)
                } ?: CategoriaDto(categoriaId, "")

                val dto = ProductoDto(
                    idProducto = id,
                    nombre = nombre,
                    precio = precio,
                    stock = stock,
                    estado = estado.name,
                    categoria = categoriaDto,
                    imagenUrl = imagenUri
                )

                // Enviar a API
                val actualizado = remoteRepository?.actualizarProductoRemoto(dto)

                if (actualizado != null) {
                    repository.update(actualizado)
                } else {
                    repository.update(
                        Producto(
                            idProducto = id,
                            nombre = nombre,
                            precio = precio,
                            stock = stock,
                            categoriaId = categoriaId,
                            estado = estado,
                            imagenResId = imagenResId,
                            imagenUri = imagenUri
                        )
                    )
                }

                limpiarFormulario("Producto actualizado")

            } catch (e: Exception) {
                setError("Error al actualizar: ${e.message}")
            }
        }
    }


    // ------------------------------------------------------
    // ELIMINAR PRODUCTO
    // ------------------------------------------------------
    fun deleteProducto(idProducto: Long) {
        viewModelScope.launch {
            try {
                remoteRepository?.eliminarProductoRemoto(idProducto)
            } catch (_: Exception) {}

            repository.deleteById(idProducto)
            setSuccess("Producto eliminado")
        }
    }

    // ------------------------------------------------------
    // CAMBIAR ESTADO DESDE ANDROID (DTO)
    // ------------------------------------------------------
    fun cambiarEstadoManual(idProducto: Long, nuevoEstado: EstadoProducto) {
        viewModelScope.launch {
            try {
                val estadoReq = EstadoRequest(nuevoEstado.name)

                remoteRepository?.cambiarEstadoRemoto(idProducto, estadoReq)

                val actual = state.value.productos.firstOrNull { it.idProducto == idProducto }
                    ?: return@launch setError("Producto no encontrado.")

                val actualizado = actual.copy(estado = nuevoEstado)
                repository.update(actualizado)

                setSuccess("Estado actualizado correctamente")

            } catch (e: Exception) {
                setError("Error al cambiar estado: ${e.message}")
            }
        }
    }

    // ------------------------------------------------------
    // ACTUALIZACIONES DEL FORMULARIO
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

    // ------------------------------------------------------
    // UTILIDADES
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

    fun limpiarMensajes() {
        _state.value = _state.value.copy(errorMsg = null, successMsg = null)
    }

    private fun setError(msg: String) {
        _state.value = _state.value.copy(errorMsg = msg, successMsg = null)
    }

    private fun setSuccess(msg: String) {
        _state.value = _state.value.copy(successMsg = msg, errorMsg = null)
    }
}

// ----------------------------------------------------------
// FACTORY DEL VIEWMODEL (CORRECTO)
// ----------------------------------------------------------
class ProductoViewModelFactory(
    private val repository: ProductoRepository,
    private val categoriaRepository: CategoriaRepository,
    private val remoteRepository: ProductoRemoteRepository?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductoViewModel(repository, categoriaRepository, remoteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
