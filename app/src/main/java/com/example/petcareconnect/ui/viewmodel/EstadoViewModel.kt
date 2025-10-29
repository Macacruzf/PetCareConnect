package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.petcareconnect.data.model.Estado
import com.example.petcareconnect.data.repository.EstadoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------------
// EstadoViewModel.kt
// ---------------------------------------------------------------------------
// Este ViewModel gestiona la carga y observación de los "estados" disponibles
// en el sistema PetCare Connect (por ejemplo, estados de pedidos o productos).
// Su objetivo es mantener la interfaz sincronizada con los datos del repositorio,
// garantizando actualizaciones automáticas y eficientes mediante flujos reactivos.
// ---------------------------------------------------------------------------


// ---------------------- ESTADO DE INTERFAZ ----------------------
// Define la información observable que la UI consumirá para renderizar la vista.
data class EstadoUiState(
    val estados: List<Estado> = emptyList(), // Lista de estados cargados desde la base de datos
    val isLoading: Boolean = true            // Indica si los datos aún se están cargando
)


// ---------------------- VIEWMODEL PRINCIPAL ----------------------
// Controla el flujo de datos y coordina las llamadas al repositorio.
// Utiliza corrutinas para ejecutar operaciones asincrónicas y StateFlow
// para emitir cambios de forma reactiva hacia la UI.
class EstadoViewModel(private val repository: EstadoRepository) : ViewModel() {

    // Flujo interno de estado que contiene los datos actuales del UI state.
    private val _state = MutableStateFlow(EstadoUiState())

    // Exposición pública de solo lectura del flujo (para mantener la inmutabilidad).
    val state: StateFlow<EstadoUiState> = _state

    // Bloque de inicialización que carga los estados al crear el ViewModel.
    init {
        loadEstados()
    }


    // ---------------------- CARGAR ESTADOS ----------------------
    // Recupera los estados almacenados en la base de datos local (Room)
    // y los emite a través del flujo para que Compose actualice la interfaz.
    //
    // ANIMACIÓN REACTIVA:
    // Al emitir nuevos valores en _state, los componentes que usan collectAsState()
    // (por ejemplo, LazyColumn o Text en la UI) se recomponen automáticamente.
    // Esta recomposición genera transiciones visuales suaves al mostrar o actualizar datos.
    private fun loadEstados() {
        viewModelScope.launch {
            repository.getAllEstados().collect { lista ->
                _state.value = EstadoUiState(
                    estados = lista,
                    isLoading = false // Cambia a falso una vez que se completa la carga
                )
            }
        }
    }
}


// ---------------------- FACTORY DEL VIEWMODEL ----------------------
// Permite crear instancias del EstadoViewModel inyectando el repositorio
// correspondiente. Esto es útil para mantener la arquitectura limpia y
// compatible con herramientas de inyección de dependencias y pruebas unitarias.
class EstadoViewModelFactory(private val repository: EstadoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EstadoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EstadoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
