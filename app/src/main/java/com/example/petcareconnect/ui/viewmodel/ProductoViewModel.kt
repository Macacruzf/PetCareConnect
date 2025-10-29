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

/*
 * ---------------------------------------------------------------------------
 * ProductoViewModel.kt
 * ---------------------------------------------------------------------------
 * Este ViewModel administra la lógica de negocio relacionada con los productos
 * del sistema PetCare Connect, incluyendo:
 *  - Carga de productos y categorías.
 *  - Inserción y eliminación de productos.
 *  - Actualización reactiva de datos para Compose.
 *
 * Se apoya en corutinas (viewModelScope) y en el patrón de estado inmutable
 * (StateFlow) para ofrecer una experiencia fluida y reactiva en la interfaz.
 * ---------------------------------------------------------------------------
 */


// ---------------------------------------------------------------------------
// ESTADO DE UI (ProductoUiState)
// ---------------------------------------------------------------------------
// Representa el estado observable de la interfaz. Cada cambio en sus valores
// provoca una recomposición automática de la UI (animación de actualización).
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


// ---------------------------------------------------------------------------
// VIEWMODEL
// ---------------------------------------------------------------------------
// Controla el flujo completo de datos del módulo de productos.
// Se comunica con los repositorios de Producto y Categoría.
// Implementa una sincronización automática con Compose usando StateFlow.
class ProductoViewModel(
    private val repository: ProductoRepository,
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    // Flujo de estado observable desde la UI.
    private val _state = MutableStateFlow(ProductoUiState())
    val state: StateFlow<ProductoUiState> = _state

    // Al inicializar el ViewModel, se cargan productos y categorías.
    init {
        loadProductos()
        loadCategorias()
    }


    // -----------------------------------------------------------------------
    // CARGAR PRODUCTOS
    // -----------------------------------------------------------------------
    // Recupera todos los productos desde el repositorio.
    // Cada vez que se actualiza la lista, Jetpack Compose se recompone
    // de manera animada, mostrando los cambios sin recargar manualmente.
    fun loadProductos() {
        viewModelScope.launch {
            repository.getAllProductos().collect { productos ->
                _state.value = _state.value.copy(productos = productos)
            }
        }
    }


    // -----------------------------------------------------------------------
    // CARGAR CATEGORÍAS (CON REINTENTO)
    // -----------------------------------------------------------------------
    // Este método observa las categorías desde el repositorio.
    // Si no existen, espera brevemente (animación de carga) y reintenta.
    //
    // La recomposición de la UI mostrará el estado “cargando” y luego,
    // al recibir los datos, transicionará suavemente al contenido final.
    private fun loadCategorias() {
        viewModelScope.launch {
            categoriaRepository.getAllCategorias().collect { categorias ->
                if (categorias.isEmpty()) {
                    println("No se encontraron categorías al inicio. Reintentando...")
                    delay(800) // Simula una breve animación de carga
                    val retryCategorias = categoriaRepository.getAllOnce()
                    println("Categorías recargadas: ${retryCategorias.map { it.nombre }}")
                    _state.value = _state.value.copy(categorias = retryCategorias)
                } else {
                    println("Categorías cargadas correctamente: ${categorias.map { it.nombre }}")
                    _state.value = _state.value.copy(categorias = categorias)
                }
            }
        }
    }


    // -----------------------------------------------------------------------
    // RECARGAR CATEGORÍAS MANUALMENTE
    // -----------------------------------------------------------------------
    // Se usa desde la UI si el flujo de categorías no entrega datos a tiempo.
    // Dispara una animación reactiva de actualización en la lista desplegable.
    fun recargarCategoriasManualmente() {
        viewModelScope.launch {
            val categorias = categoriaRepository.getAllOnce()
            if (categorias.isNotEmpty()) {
                _state.value = _state.value.copy(categorias = categorias)
                println("Categorías recargadas manualmente: ${categorias.map { it.nombre }}")
            } else {
                println("No se encontraron categorías en la base de datos.")
            }
        }
    }


    // -----------------------------------------------------------------------
    // INSERTAR PRODUCTO
    // -----------------------------------------------------------------------
    // Inserta un nuevo producto validando los campos básicos.
    // Al finalizar, emite un nuevo estado con mensaje de éxito o error.
    //
    // ANIMACIÓN REACTIVA:
    // Cuando se inserta el producto, las pantallas que usan LazyColumn
    // o cualquier elemento dependiente de state.productos se recomponen
    // automáticamente, mostrando la nueva entrada con una transición fluida.
    fun insertProducto() {
        val nombre = _state.value.nombre.trim()
        val precio = _state.value.precio.toDoubleOrNull()
        val stock = _state.value.stock.toIntOrNull()
        val categoriaId = _state.value.categoriaId
        val estadoId = _state.value.estadoId ?: 1
        val imagenResId = _state.value.imagenResId

        // Validaciones básicas de entrada.
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

            // Se emite un nuevo estado: limpia los campos y muestra mensaje.
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


    // -----------------------------------------------------------------------
    // ELIMINAR PRODUCTO
    // -----------------------------------------------------------------------
    // Elimina un producto según su ID. Compose actualiza la lista visible
    // de manera reactiva y automática.
    fun deleteProducto(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }


    // -----------------------------------------------------------------------
    // MANEJADORES DE CAMBIOS DE CAMPO
    // -----------------------------------------------------------------------
    // Estas funciones actualizan campos individuales dentro del estado.
    // Cada una genera una recomposición instantánea, simulando una animación
    // de escritura o actualización visual mientras el usuario interactúa.
    fun onNombreChange(value: String) { _state.value = _state.value.copy(nombre = value) }
    fun onPrecioChange(value: String) { _state.value = _state.value.copy(precio = value) }
    fun onStockChange(value: String) { _state.value = _state.value.copy(stock = value) }
    fun onCategoriaChange(value: Int) { _state.value = _state.value.copy(categoriaId = value) }
    fun onImagenChange(resId: Int?) { _state.value = _state.value.copy(imagenResId = resId) }


    // -----------------------------------------------------------------------
    // ACTUALIZAR CATEGORÍAS EXTERNAMENTE
    // -----------------------------------------------------------------------
    // Permite actualizar la lista de categorías desde un componente superior,
    // generando la animación de recomposición sin intervención directa del ViewModel.
    fun onCategoriasCargadas(categorias: List<Categoria>) {
        _state.value = _state.value.copy(categorias = categorias)
    }
}


// ---------------------------------------------------------------------------
// FACTORY DEL VIEWMODEL
// ---------------------------------------------------------------------------
// Permite crear instancias de ProductoViewModel con dependencias inyectadas.
// Se usa cuando el ViewModel requiere parámetros externos (repositorios).
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
