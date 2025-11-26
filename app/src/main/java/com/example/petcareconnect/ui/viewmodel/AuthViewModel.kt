package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.petcareconnect.data.model.Usuario
import com.example.petcareconnect.data.remote.ApiModule
import com.example.petcareconnect.data.remote.dto.*
import com.example.petcareconnect.data.session.UserSession
import com.example.petcareconnect.domain.validation.*
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
    val rol: String = "CLIENTE",

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
//                         VIEWMODEL
// -------------------------------------------------------------
class AuthViewModel : ViewModel() {

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    private val _allUsers = MutableStateFlow<List<Usuario>>(emptyList())
    val allUsers: StateFlow<List<Usuario>> = _allUsers

    // -------------------------------------------------------------
    fun clearLoginResult() {
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMsg = null) }
    }

    // -------------------------------------------------------------
    // 🚪 LOGOUT
    // -------------------------------------------------------------
    fun logout() {
        UserSession.clear()
        _currentUser.value = null
        _userRole.value = null

        resetLoginForm()
        resetRegisterForm()
    }

    // -------------------------------------------------------------
    // LISTAR USUARIOS (ADMIN)
    // -------------------------------------------------------------
    fun cargarUsuarios() {
        viewModelScope.launch {
            try {
                val adminId = UserSession.usuarioId ?: return@launch
                val remotos = ApiModule.usuarioApi.listarUsuarios(adminId)
                _allUsers.value = remotos.map { it.toLocalModel() }
            } catch (e: Exception) {
                println("Error al cargar usuarios: ${e.message}")
            }
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            try {
                ApiModule.usuarioApi.deleteUser(id)
                _allUsers.value = _allUsers.value.filter { it.idUsuario != id }
            } catch (e: Exception) {
                println("Error al eliminar usuario: ${e.message}")
            }
        }
    }

    // -------------------------------------------------------------
    // UPDATE USER ADMIN
    // -------------------------------------------------------------
    fun updateUserAdmin(usuario: Usuario, onResult: (String?) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val body = UsuarioRemote(
                    idUsuario = usuario.idUsuario,
                    nombreUsuario = usuario.nombreUsuario,
                    email = usuario.email,
                    telefono = usuario.telefono,
                    password = usuario.password,
                    rol = usuario.rol,
                    estado = usuario.estado
                )

                val actualizado = ApiModule.usuarioApi.updatePerfil(usuario.idUsuario, body)

                _allUsers.update { lista ->
                    lista.map {
                        if (it.idUsuario == usuario.idUsuario) actualizado.toLocalModel()
                        else it
                    }
                }

                onResult(null)

            } catch (e: Exception) {
                onResult("Error al actualizar usuario: ${e.message}")
            }
        }
    }

    // -------------------------------------------------------------
    // LOGIN SETTERS
    // -------------------------------------------------------------
    fun onLoginEmailChange(value: String) {
        _login.update { it.copy(email = value, emailError = validateEmail(value)) }
        validateLoginReady()
    }

    fun onLoginPassChange(value: String) {
        _login.update { it.copy(pass = value) }
        validateLoginReady()
    }

    private fun validateLoginReady() {
        val s = _login.value
        val ok = s.emailError == null &&
                s.email.isNotBlank() &&
                s.pass.isNotBlank()

        _login.update { it.copy(canSubmit = ok) }
    }

    fun submitLogin() = submitLoginRemote()

    // -------------------------------------------------------------
    // SUBMIT LOGIN
    // -------------------------------------------------------------
    fun submitLoginRemote() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {

            _login.update { it.copy(isSubmitting = true, errorMsg = null) }

            try {
                val resp = ApiModule.usuarioApi.login(
                    LoginRequest(
                        email = s.email.trim().lowercase(),
                        password = s.pass.trim()
                    )
                )

                val uRemote = resp.usuario ?: error("Respuesta inválida del servidor")

                if (uRemote.estado != "ACTIVO") {
                    _login.update {
                        it.copy(
                            isSubmitting = false,
                            success = false,
                            errorMsg = "Tu cuenta está ${uRemote.estado.lowercase()}."
                        )
                    }
                    return@launch
                }

                val local = uRemote.toLocalModel()
                _currentUser.value = local

                UserSession.usuarioId = uRemote.idUsuario
                UserSession.rol = uRemote.rol
                UserSession.estado = uRemote.estado

                _userRole.value = uRemote.rol

                _login.update {
                    it.copy(
                        isSubmitting = false,
                        success = true,
                        usuario = local,
                        rol = local.rol
                    )
                }

            } catch (e: Exception) {

                val msg = when {
                    e.message?.contains("401") == true -> "Correo o contraseña incorrectos"
                    e.message?.contains("404") == true -> "Usuario no encontrado"
                    else -> "No se pudo iniciar sesión. Intenta nuevamente."
                }

                _login.update {
                    it.copy(isSubmitting = false, success = false, errorMsg = msg)
                }
            }
        }
    }

    fun resetLoginForm() {
        _login.value = LoginUiState()
    }

    // -------------------------------------------------------------
    // REGISTRO SETTERS
    // -------------------------------------------------------------
    fun onNameChange(v: String) {
        _register.update { it.copy(name = v, nameError = validateNameLettersOnly(v)) }
        validateRegisterReady()
    }

    fun onRegisterEmailChange(v: String) {
        _register.update { it.copy(email = v, emailError = validateEmail(v)) }
        validateRegisterReady()
    }

    fun onPhoneChange(v: String) {
        _register.update { it.copy(phone = v, phoneError = validatePhoneDigitsOnly(v)) }
        validateRegisterReady()
    }

    fun onRegisterPassChange(v: String) {
        _register.update { it.copy(pass = v, passError = validateStrongPassword(v)) }
        validateRegisterReady()
    }

    fun onConfirmChange(v: String) {
        _register.update {
            it.copy(confirm = v, confirmError = validateConfirm(_register.value.pass, v))
        }
        validateRegisterReady()
    }

    fun onFotoSelected(uri: String) {
        _register.update { it.copy(fotoUri = uri) }
    }

    fun onRolChange(r: String) {
        _register.update { it.copy(rol = r) }
    }

    private fun validateRegisterReady() {
        val s = _register.value

        val ok = s.nameError == null &&
                s.emailError == null &&
                s.phoneError == null &&
                s.passError == null &&
                s.confirmError == null &&
                s.name.isNotBlank() &&
                s.email.isNotBlank() &&
                s.phone.isNotBlank() &&
                s.pass.isNotBlank() &&
                s.confirm.isNotBlank()

        _register.update { it.copy(canSubmit = ok) }
    }

    // -------------------------------------------------------------
    // SUBMIT REGISTRO (YA NO LOGUEA AUTOMÁTICAMENTE)
    // -------------------------------------------------------------
    fun submitRegister() = submitRegisterRemote()

    fun submitRegisterRemote() {
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {

            _register.update { it.copy(isSubmitting = true, errorMsg = null) }

            try {
                ApiModule.usuarioApi.register(
                    RegisterRequest(
                        nombreUsuario = s.name.trim(),
                        email = s.email.trim().lowercase(),
                        telefono = s.phone.trim(),
                        password = s.pass.trim(),
                        rol = s.rol
                    )
                )

                // ⭐ IMPORTANTE: el usuario YA NO queda logueado
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

    // -------------------------------------------------------------
    // UPDATE PERFIL CLIENTE
    // -------------------------------------------------------------
    fun updateUser(
        email: String,
        telefono: String,
        password: String?,
        onResult: (String?) -> Unit
    ) {
        val id = UserSession.usuarioId ?: return onResult("Usuario no autenticado")

        viewModelScope.launch {
            try {
                val usuarioActual = _currentUser.value
                    ?: return@launch onResult("No se pudo cargar el usuario actual")

                val body = UsuarioRemote(
                    idUsuario = id,
                    nombreUsuario = usuarioActual.nombreUsuario,
                    email = email,
                    telefono = telefono,
                    password = password,
                    rol = usuarioActual.rol,
                    estado = usuarioActual.estado
                )

                val actualizado = ApiModule.usuarioApi.updatePerfil(id, body)
                _currentUser.value = actualizado.toLocalModel()

                onResult(null)

            } catch (e: Exception) {
                onResult("Error al actualizar: ${e.message}")
            }
        }
    }
}

// -------------------------------------------------------------
// REMOTO → LOCAL
// -------------------------------------------------------------
fun UsuarioRemote.toLocalModel(): Usuario {
    return Usuario(
        idUsuario = this.idUsuario,
        nombreUsuario = this.nombreUsuario,
        email = this.email,
        telefono = this.telefono,
        password = this.password ?: "",
        rol = this.rol,
        estado = this.estado
    )
}

// -------------------------------------------------------------
// FACTORY
// -------------------------------------------------------------
class AuthViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
