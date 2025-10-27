package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.model.Categoria
import com.example.petcareconnect.data.repository.ProductoRepository
import com.example.petcareconnect.data.repository.CategoriaRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProductoUiState(
    val productos: List<Producto> = emptyList(),
    val categorias: List<Categoria> = emptyList(),
    val nombre: String = "",
    val precio: String = "",
    val stock: String = "",
    val categoriaId: Int? = null,
    val estadoId: Int? = null,
    val imagenResId: Int? = null,
    val isLoading: Boolean = false,
    val errorMsg: String? = null,
    val successMsg: String? = null
)

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

    // Cargar productos
    fun loadProductos() {
        viewModelScope.launch {
            repository.getAllProductos().collect { productos ->
                _state.value = _state.value.copy(productos = productos)
            }
        }
    }

    // Cargar categorías (con reintento si aún no existen)
    private fun loadCategorias() {
        viewModelScope.launch {
            categoriaRepository.getAllCategorias().collect { categorias ->
                if (categorias.isEmpty()) {
                    println("No se encontraron categorías al inicio. Reintentando...")
                    delay(800) // ⏳ espera un poco mientras se inserta la semilla
                    val retryCategorias = categoriaRepository.getAllOnce()
                    println(" Categorías recargadas: ${retryCategorias.map { it.nombre }}")
                    _state.value = _state.value.copy(categorias = retryCategorias)
                } else {
                    println(" Categorías cargadas correctamente: ${categorias.map { it.nombre }}")
                    _state.value = _state.value.copy(categorias = categorias)
                }
            }
        }
    }

    //  Forzar carga manual de categorías si el Flow no las entrega a tiempo
    fun recargarCategoriasManualmente() {
        viewModelScope.launch {
            val categorias = categoriaRepository.getAllOnce()
            if (categorias.isNotEmpty()) {
                _state.value = _state.value.copy(categorias = categorias)
                println("Categorías recargadas manualmente: ${categorias.map { it.nombre }}")
            } else {
                println(" No se encontraron categorías en la base de datos.")
            }
        }
    }

    //  Insertar producto con imagen (de drawable)
    fun insertProducto() {
        val nombre = _state.value.nombre.trim()
        val precio = _state.value.precio.toDoubleOrNull()
        val stock = _state.value.stock.toIntOrNull()
        val categoriaId = _state.value.categoriaId
        val estadoId = _state.value.estadoId ?: 1
        val imagenResId = _state.value.imagenResId

        if (nombre.isEmpty() || precio == null || stock == null || categoriaId == null) {
            _state.value = _state.value.copy(errorMsg = "Completa todos los campos correctamente.")
            return
        }

        viewModelScope.launch {
            repository.insert(
                Producto(
                    nombre = nombre,
                    precio = precio,
                    stock = stock,
                    categoriaId = categoriaId,
                    estadoId = estadoId,
                    imagenResId = imagenResId
                )
            )
            _state.value = _state.value.copy(
                successMsg = "Producto guardado correctamente.",
                errorMsg = null,
                nombre = "",
                precio = "",
                stock = "",
                categoriaId = null,
                imagenResId = null
            )
        }
    }

    //  Eliminar producto
    fun deleteProducto(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    //  Manejadores de cambios
    fun onNombreChange(value: String) { _state.value = _state.value.copy(nombre = value) }
    fun onPrecioChange(value: String) { _state.value = _state.value.copy(precio = value) }
    fun onStockChange(value: String) { _state.value = _state.value.copy(stock = value) }
    fun onCategoriaChange(value: Int) { _state.value = _state.value.copy(categoriaId = value) }
    fun onImagenChange(resId: Int?) { _state.value = _state.value.copy(imagenResId = resId) }

    //  Actualiza categorías si se pasan desde la UI
    fun onCategoriasCargadas(categorias: List<Categoria>) {
        _state.value = _state.value.copy(categorias = categorias)
    }
}

//  Factory para crear el ViewModel
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
