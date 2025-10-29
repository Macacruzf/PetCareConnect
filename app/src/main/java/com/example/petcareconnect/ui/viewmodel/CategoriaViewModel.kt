package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.petcareconnect.data.model.Categoria
import com.example.petcareconnect.data.repository.CategoriaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------------
// CategoriaViewModel.kt
// ---------------------------------------------------------------------------
// Este ViewModel controla la lógica de negocio asociada a las categorías
// de productos dentro del sistema PetCare Connect.
// Gestiona la creación, actualización, eliminación y listado de categorías,
// manteniendo sincronizada la interfaz con la base de datos Room en tiempo real.
// ---------------------------------------------------------------------------


// ---------------------- ESTADO DE INTERFAZ ----------------------
// Representa los datos observables que la UI utilizará para renderizar
// la lista de categorías y los mensajes de error o campos de entrada.
data class CategoriaUiState(
    val categorias: List<Categoria> = emptyList(), // Lista de categorías cargadas
    val nombre: String = "",                      // Valor del campo de texto para agregar/editar
    val errorMsg: String? = null                  // Mensaje de error si se deja el nombre vacío
)


// ---------------------- VIEWMODEL PRINCIPAL ----------------------
class CategoriaViewModel(private val repository: CategoriaRepository) : ViewModel() {

    // Flujo de estado reactivo que emite los cambios del UI state.
    // Compose volverá a dibujar automáticamente la pantalla cuando el estado cambie.
    private val _state = MutableStateFlow(CategoriaUiState())
    val state: StateFlow<CategoriaUiState> = _state

    // Bloque inicial que carga las categorías al crear el ViewModel.
    init {
        loadCategorias()
    }


    // ---------------------- CARGAR CATEGORÍAS ----------------------
    // Recupera todas las categorías desde la base de datos local (Room)
    // y actualiza el flujo de estado con la lista más reciente.
    //
    // ANIMACIÓN REACTIVA:
    // Al actualizar el flujo, los componentes que usan `collectAsState()`
    // (por ejemplo, LazyColumn en la UI) muestran los cambios de forma fluida
    // sin necesidad de recargar manualmente.
    private fun loadCategorias() {
        viewModelScope.launch {
            repository.getAllCategorias().collect { categorias ->
                _state.value = _state.value.copy(categorias = categorias)
            }
        }
    }


    // ---------------------- INSERTAR CATEGORÍA ----------------------
    // Valida que el campo nombre no esté vacío, y luego inserta una nueva
    // categoría en la base de datos.
    //
    // ANIMACIÓN REACTIVA:
    // Al insertarse una nueva categoría, `getAllCategorias()` emite un nuevo valor,
    // provocando que la lista en pantalla se actualice instantáneamente.
    fun insertCategoria() {
        val nombre = _state.value.nombre.trim()

        // Validación del campo vacío
        if (nombre.isEmpty()) {
            _state.value = _state.value.copy(errorMsg = "Debes ingresar un nombre válido.")
            return
        }

        // Inserción asincrónica en la base de datos
        viewModelScope.launch {
            repository.insert(Categoria(nombre = nombre))
            // Reinicia el campo de texto y limpia mensajes de error
            _state.value = _state.value.copy(nombre = "", errorMsg = null)
        }
    }


    // ---------------------- ACTUALIZAR CATEGORÍA ----------------------
    // Actualiza el nombre de una categoría existente.
    //
    // ANIMACIÓN REACTIVA:
    // Al modificarse un registro, el flujo de Room detecta el cambio
    // y Compose vuelve a renderizar la lista con la nueva información.
    fun updateCategoria(id: Int, nuevoNombre: String) {
        if (nuevoNombre.isBlank()) return

        viewModelScope.launch {
            repository.update(Categoria(idCategoria = id, nombre = nuevoNombre))
        }
    }


    // ---------------------- ELIMINAR CATEGORÍA ----------------------
    // Elimina una categoría de la base de datos según su identificador.
    //
    // ANIMACIÓN REACTIVA:
    // La eliminación provoca una emisión inmediata en el flujo `getAllCategorias()`,
    // actualizando la lista visible sin intervención manual.
    fun deleteCategoria(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }


    // ---------------------- ACTUALIZAR CAMPO DE TEXTO ----------------------
    // Mantiene sincronizado el valor del campo de texto en la UI
    // con el estado interno del ViewModel.
    fun onNombreChange(value: String) {
        _state.value = _state.value.copy(nombre = value)
    }
}


// ---------------------- FACTORY DE VIEWMODEL ----------------------
// Permite la creación del ViewModel inyectando el repositorio correspondiente.
// Esto facilita pruebas unitarias e integración con otros componentes de arquitectura.
class CategoriaViewModelFactory(
    private val repository: CategoriaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoriaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
