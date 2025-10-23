package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.petcareconnect.data.model.Categoria
import com.example.petcareconnect.data.repository.CategoriaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// -----------------------------
// UI STATE (estado observable)
// -----------------------------
data class CategoriaUiState(
    val categorias: List<Categoria> = emptyList(),
    val nombre: String = "",
    val errorMsg: String? = null
)

// -----------------------------
// VIEWMODEL
// -----------------------------
class CategoriaViewModel(private val repository: CategoriaRepository) : ViewModel() {

    private val _state = MutableStateFlow(CategoriaUiState())
    val state: StateFlow<CategoriaUiState> = _state

    init {
        loadCategorias()
    }

    // Cargar todas las categorías
    private fun loadCategorias() {
        viewModelScope.launch {
            repository.getAllCategorias().collect { categorias ->
                _state.value = _state.value.copy(categorias = categorias)
            }
        }
    }

    // Insertar nueva categoría
    fun insertCategoria() {
        val nombre = _state.value.nombre.trim()
        if (nombre.isEmpty()) {
            _state.value = _state.value.copy(errorMsg = "Debes ingresar un nombre válido.")
            return
        }

        viewModelScope.launch {
            repository.insert(Categoria(nombre = nombre))
            _state.value = _state.value.copy(nombre = "", errorMsg = null)
        }
    }

    // Actualizar nombre de categoría existente
    fun updateCategoria(id: Int, nuevoNombre: String) {
        if (nuevoNombre.isBlank()) return
        viewModelScope.launch {
            repository.update(Categoria(idCategoria = id, nombre = nuevoNombre))
        }
    }

    // Eliminar categoría por ID
    fun deleteCategoria(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    // Actualizar campo de texto
    fun onNombreChange(value: String) {
        _state.value = _state.value.copy(nombre = value)
    }
}

// -----------------------------
// FACTORY (inyección de dependencias)
// -----------------------------
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