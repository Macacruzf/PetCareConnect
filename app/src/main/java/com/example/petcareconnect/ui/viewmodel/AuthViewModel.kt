package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.petcareconnect.data.model.Usuario
import com.example.petcareconnect.data.repository.UsuarioRepository
import com.example.petcareconnect.domain.validation.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// -------------------------------------------------------------
//                        ESTADOS UI
// -------------------------------------------------------------
data class LoginUiState(
    val email: String = "",
    val pass: String = "",
    val emailError: String? = null,
    val passError: String? = null,
    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null,
    val rol: String? = null,
    val usuario: Usuario? = null
)

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val pass: String = "",
    val confirm: String = "",
    val fotoUri: String? = null,
    val rol: String? = "CLIENTE",

    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passError: String? = null,
    val confirmError: String? = null,

    val isSubmitting: Boolean = false,
    val canSubmit: Boolean = false,
    val success: Boolean = false,
    val errorMsg: String? = null
)

// -------------------------------------------------------------
//                     MAIN VIEWMODEL
// -------------------------------------------------------------
class AuthViewModel(
    private val repository: UsuarioRepository? = null
) : ViewModel() {

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser

    // -------------------------------------------------------------
    //                            LOGIN
    // -------------------------------------------------------------
    fun onLoginEmailChange(value: String) {
        _login.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeLoginCanSubmit()
    }

    fun onLoginPassChange(value: String) {
        _login.update { it.copy(pass = value) }
        recomputeLoginCanSubmit()
    }

    private fun recomputeLoginCanSubmit() {
        val s = _login.value
        val ok = s.emailError == null && s.email.isNotBlank() && s.pass.isNotBlank()
        _login.update { it.copy(canSubmit = ok) }
    }

    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null) }

            try {
                val email = s.email.trim().lowercase()
                val pass = s.pass.trim()

                val user = repository?.login(email, pass)

                delay(400)

                if (user == null) {
                    _login.update {
                        it.copy(
                            isSubmitting = false,
                            success = false,
                            errorMsg = "Credenciales inválidas"
                        )
                    }
                } else {

                    _currentUser.value = user
                    _userRole.value = user.rol

                    _login.update {
                        it.copy(
                            isSubmitting = false,
                            success = true,
                            usuario = user,
                            rol = user.rol
                        )
                    }
                }

            } catch (e: Exception) {
                _login.update {
                    it.copy(
                        isSubmitting = false,
                        errorMsg = "Error de conexión: ${e.message}"
                    )
                }
            }
        }
    }

    fun resetLoginForm() {
        _login.value = LoginUiState()
    }

    // ⭐ Limpia el estado luego de un login exitoso
    fun clearLoginResult() {
        _login.update {
            it.copy(
                success = false,
                errorMsg = null,
                isSubmitting = false
            )
        }
    }

    fun logout() {
        _currentUser.value = null
        _userRole.value = null
        resetLoginForm()
    }

    // -------------------------------------------------------------
    //                      REGISTRO
    // -------------------------------------------------------------
    fun onNameChange(value: String) {
        val filtered = value.filter { it.isLetter() || it.isWhitespace() }
        _register.update { it.copy(name = filtered, nameError = validateNameLettersOnly(filtered)) }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterEmailChange(value: String) {
        _register.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeRegisterCanSubmit()
    }

    fun onPhoneChange(value: String) {
        val digits = value.filter { it.isDigit() }
        _register.update { it.copy(phone = digits, phoneError = validatePhoneDigitsOnly(digits)) }
        recomputeRegisterCanSubmit()
    }

    fun onRegisterPassChange(value: String) {
        _register.update { it.copy(pass = value, passError = validateStrongPassword(value)) }
        _register.update { it.copy(confirmError = validateConfirm(it.pass, it.confirm)) }
        recomputeRegisterCanSubmit()
    }

    fun onConfirmChange(value: String) {
        _register.update { it.copy(confirm = value, confirmError = validateConfirm(it.pass, value)) }
        recomputeRegisterCanSubmit()
    }

    fun onFotoSelected(uri: String) {
        _register.update { it.copy(fotoUri = uri) }
    }

    fun onRolChange(newRol: String) {
        _register.update { it.copy(rol = newRol) }
    }

    private fun recomputeRegisterCanSubmit() {
        val s = _register.value
        val noErrors = listOf(
            s.nameError, s.emailError, s.phoneError, s.passError, s.confirmError
        ).all { it == null }

        val filled = s.name.isNotBlank() &&
                s.email.isNotBlank() &&
                s.phone.isNotBlank() &&
                s.pass.isNotBlank() &&
                s.confirm.isNotBlank()

        _register.update { it.copy(canSubmit = noErrors && filled) }
    }

    fun submitRegister() {
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            try {
                val email = s.email.trim().lowercase()

                val exists = repository?.getByEmail(email)
                if (exists != null) {
                    _register.update {
                        it.copy(
                            isSubmitting = false,
                            success = false,
                            errorMsg = "El correo ya está registrado"
                        )
                    }
                    return@launch
                }

                repository?.insert(
                    Usuario(
                        nombreUsuario = s.name.trim(),
                        email = email,
                        telefono = s.phone.trim(),
                        password = s.pass.trim(),
                        rol = s.rol ?: "CLIENTE",
                        fotoUri = s.fotoUri
                    )
                )

                delay(300)

                _register.update { it.copy(success = true, isSubmitting = false) }

            } catch (e: Exception) {
                _register.update {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = "Error al registrar: ${e.message}"
                    )
                }
            }
        }
    }

    fun resetRegisterForm() {
        _register.value = RegisterUiState()
    }

    // ⭐ Limpia el estado luego de registrarse exitosamente
    fun clearRegisterResult() {
        _register.update {
            it.copy(
                success = false,
                errorMsg = null,
                isSubmitting = false
            )
        }
    }

    // -------------------------------------------------------------
    //                     EDITAR PERFIL CLIENTE
    // -------------------------------------------------------------
    fun updateUser(nombre: String?, email: String?, telefono: String?, pass: String?) {
        val actual = _currentUser.value ?: return

        val actualizado = actual.copy(
            nombreUsuario = nombre ?: actual.nombreUsuario,
            email = email ?: actual.email,
            telefono = telefono ?: actual.telefono,
            password = pass ?: actual.password
        )

        viewModelScope.launch {
            try {
                repository?.update(actualizado)
                _currentUser.value = actualizado
            } catch (e: Exception) {
                println("Error actualizando usuario: ${e.message}")
            }
        }
    }

    fun updateUserFoto(newUri: String?) {
        val actual = _currentUser.value ?: return
        val actualizado = actual.copy(fotoUri = newUri)
        viewModelScope.launch {
            repository?.update(actualizado)
            _currentUser.value = actualizado
        }
    }

    // -------------------------------------------------------------
    //                     ADMIN → LISTAR USUARIOS
    // -------------------------------------------------------------
    private val _allUsers = MutableStateFlow<List<Usuario>>(emptyList())
    val allUsers: StateFlow<List<Usuario>> = _allUsers

    fun cargarUsuarios() {
        if (repository == null) return
        viewModelScope.launch {
            repository.getAllUsuarios().collect { lista ->
                _allUsers.value = lista
            }
        }
    }

    // -------------------------------------------------------------
    //                     ADMIN → ELIMINAR USUARIO
    // -------------------------------------------------------------
    fun deleteUser(id: Int) {
        if (repository == null) return
        viewModelScope.launch {
            try {
                repository.deleteById(id)
                cargarUsuarios()
            } catch (e: Exception) {
                println("Error eliminando usuario: ${e.message}")
            }
        }
    }

    // -------------------------------------------------------------
    //                     ADMIN → EDITAR USUARIO
    // -------------------------------------------------------------
    fun updateUserAdmin(usuario: Usuario) {
        if (repository == null) return

        viewModelScope.launch {
            try {
                repository.update(usuario)
                cargarUsuarios()
            } catch (e: Exception) {
                println("Error admin al actualizar usuario: ${e.message}")
            }
        }
    }
}

// -------------------------------------------------------------
//                     FACTORY
// -------------------------------------------------------------
class AuthViewModelFactory(
    private val repository: UsuarioRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
