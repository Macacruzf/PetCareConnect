package com.example.petcareconnect.ui.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.petcareconnect.data.model.Estado
import com.example.petcareconnect.data.repository.EstadoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class EstadoUiState(
    val estados: List<Estado> = emptyList(),
    val isLoading: Boolean = true
)

class EstadoViewModel(private val repository: EstadoRepository) : ViewModel() {

    private val _state = MutableStateFlow(EstadoUiState())
    val state: StateFlow<EstadoUiState> = _state

    init {
        loadEstados()
    }

    private fun loadEstados() {
        viewModelScope.launch {
            repository.getAllEstados().collect { lista ->
                _state.value = EstadoUiState(estados = lista, isLoading = false)
            }
        }
    }
}

class EstadoViewModelFactory(private val repository: EstadoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EstadoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EstadoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}