package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.petcareconnect.data.model.Categoria
import com.example.petcareconnect.data.repository.CategoriaRepository
import com.example.petcareconnect.data.remote.CategoriaRemoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// =====================================================================
// UI STATE
// =====================================================================
data class CategoriaUiState(
    val categorias: List<Categoria> = emptyList(),
    val nombre: String = "",
    val errorMsg: String? = null,
    val successMsg: String? = null,
    val rol: String = "CLIENTE",
    val isLoading: Boolean = false
)


// =====================================================================
// VIEWMODEL (ROOM + MICROSERVICIO)
// =====================================================================
class CategoriaViewModel(
    private val repository: CategoriaRepository,
    private val remoteRepository: CategoriaRemoteRepository?,
    private val userRoleProvider: (() -> String)? = null
) : ViewModel() {

    private val _state = MutableStateFlow(CategoriaUiState())
    val state: StateFlow<CategoriaUiState> = _state

    init {
        loadRolUsuario()
        loadCategoriasLocal()
        remoteRepository?.let { syncCategorias() }   // ← sincroniza al iniciar
    }

    // ---------------------------------------------------------------
    // ROLES
    // ---------------------------------------------------------------
    private fun loadRolUsuario() {
        _state.value = _state.value.copy(
            rol = userRoleProvider?.invoke() ?: "CLIENTE"
        )
    }

    private fun esAdmin() = _state.value.rol == "ADMIN"

    // ---------------------------------------------------------------
    // ROOM - Cargar categorías locales
    // ---------------------------------------------------------------
    private fun loadCategoriasLocal() {
        viewModelScope.launch {
            repository.getAllCategorias().collect { lista ->
                _state.value = _state.value.copy(categorias = lista)
            }
        }
    }

    // ---------------------------------------------------------------
    // API - Sincronizar desde microservicio
    // ---------------------------------------------------------------
    fun syncCategorias() {
        val api = remoteRepository ?: return

        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)

                val categoriasApi = api.getCategoriasRemotas()

                repository.deleteAll()
                categoriasApi.forEach { repository.insert(it) }

                _state.value = _state.value.copy(
                    isLoading = false,
                    successMsg = "Categorías sincronizadas desde el servidor"
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMsg = "Error al sincronizar: ${e.message}"
                )
            }
        }
    }


    // ---------------------------------------------------------------
    // CREAR CATEGORÍA
    // ---------------------------------------------------------------
    fun insertCategoria() {
        if (!esAdmin()) return setError("No tienes permisos para crear categorías.")

        val nombre = _state.value.nombre.trim()
        if (nombre.isBlank()) return setError("El nombre no puede estar vacío.")

        viewModelScope.launch {
            try {
                val nuevaApi = remoteRepository?.crearCategoria(nombre)

                if (nuevaApi != null) {
                    repository.insert(nuevaApi)
                } else {
                    repository.insert(Categoria(nombre = nombre))
                }

                limpiarFormulario("Categoría agregada correctamente")

            } catch (e: Exception) {
                setError("Error al crear categoría: ${e.message}")
            }
        }
    }


    // ---------------------------------------------------------------
    // ACTUALIZAR CATEGORÍA
    // ---------------------------------------------------------------
    fun updateCategoria(id: Long, nuevoNombre: String) {
        if (!esAdmin()) return setError("No tienes permisos para editar.")

        val nombre = nuevoNombre.trim()
        if (nombre.isBlank()) return setError("El nombre no puede estar vacío.")

        viewModelScope.launch {
            try {
                val categoriaApi = remoteRepository?.actualizarCategoria(id, nombre)

                repository.update(
                    categoriaApi ?: Categoria(idCategoria = id, nombre = nombre)
                )

                setSuccess("Categoría actualizada correctamente")

            } catch (e: Exception) {
                setError("Error al actualizar categoría: ${e.message}")
            }
        }
    }


    // ---------------------------------------------------------------
    // ELIMINAR CATEGORÍA
    // ---------------------------------------------------------------
    fun deleteCategoria(id: Long) {
        if (!esAdmin()) return setError("No tienes permisos para eliminar.")

        viewModelScope.launch {
            try {
                remoteRepository?.eliminarCategoria(id)
            } catch (_: Exception) {}

            repository.deleteById(id)

            setSuccess("Categoría eliminada")
        }
    }


    // ---------------------------------------------------------------
    // EVENTOS DE UI
    // ---------------------------------------------------------------
    fun onNombreChange(value: String) {
        _state.value = _state.value.copy(nombre = value)
    }

    fun limpiarMensajes() {
        _state.value = _state.value.copy(errorMsg = null, successMsg = null)
    }


    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------
    private fun limpiarFormulario(msg: String) {
        _state.value = _state.value.copy(
            nombre = "",
            successMsg = msg,
            errorMsg = null
        )
    }

    private fun setError(msg: String) {
        _state.value = _state.value.copy(errorMsg = msg, successMsg = null)
    }

    private fun setSuccess(msg: String) {
        _state.value = _state.value.copy(successMsg = msg, errorMsg = null)
    }
}


// =====================================================================
// FACTORY
// =====================================================================
class CategoriaViewModelFactory(
    private val repository: CategoriaRepository,
    private val remoteRepository: CategoriaRemoteRepository?,
    private val roleProvider: (() -> String)? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(CategoriaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoriaViewModel(
                repository = repository,
                remoteRepository = remoteRepository,
                userRoleProvider = roleProvider
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
