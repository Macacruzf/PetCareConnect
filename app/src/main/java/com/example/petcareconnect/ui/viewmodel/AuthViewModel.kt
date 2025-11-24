package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.petcareconnect.data.model.Usuario
import com.example.petcareconnect.data.remote.ApiModule
import com.example.petcareconnect.data.remote.dto.LoginRequest
import com.example.petcareconnect.data.remote.dto.RegisterRequest
import com.example.petcareconnect.data.remote.dto.UsuarioRemote
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
//                     MAIN VIEWMODEL
// -------------------------------------------------------------
class AuthViewModel : ViewModel() {

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser

    // -------------------------------------------------------------
    //                            LOGIN
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
        val ok = s.emailError == null && s.email.isNotBlank() && s.pass.isNotBlank()
        _login.update { it.copy(canSubmit = ok) }
    }

    // 🔥 LOGIN REMOTO (MICROSERVICIO)
    fun submitLoginRemote() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null) }

            try {
                val response = ApiModule.usuarioApi.login(
                    LoginRequest(
                        email = s.email.trim().lowercase(),
                        password = s.pass.trim()
                    )
                )

                // Guardar datos globales
                UserSession.token = response.token
                UserSession.rol = response.usuario.rol
                UserSession.usuarioId = response.usuario.idUsuario?.toInt()

                val usuarioLocal = response.usuario.toLocalModel()
                _currentUser.value = usuarioLocal

                _login.update {
                    it.copy(
                        success = true,
                        isSubmitting = false,
                        errorMsg = null,
                        usuario = usuarioLocal,
                        rol = usuarioLocal.rol
                    )
                }

            } catch (e: Exception) {
                _login.update {
                    it.copy(
                        isSubmitting = false,
                        success = false,
                        errorMsg = "Error al iniciar sesión: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearLoginResult() {
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    fun resetLoginForm() {
        _login.value = LoginUiState()
    }

    // -------------------------------------------------------------
    //                          REGISTRO REMOTO
    // -------------------------------------------------------------
    fun submitRegisterRemote() {
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null) }

            try {
                val response = ApiModule.usuarioApi.register(
                    RegisterRequest(
                        nombreUsuario = s.name.trim(),
                        email = s.email.trim().lowercase(),
                        telefono = s.phone.trim(),
                        password = s.pass.trim(),
                        rol = s.rol
                    )
                )

                // Guardar sesión automática
                UserSession.token = response.token
                UserSession.rol = response.usuario.rol
                UserSession.usuarioId = response.usuario.idUsuario?.toInt()

                _currentUser.value = response.usuario.toLocalModel()

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

    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMsg = null) }
    }

    fun resetRegisterForm() {
        _register.value = RegisterUiState()
    }

}

// -------------------------------------------------------------
//            EXTENSIÓN: Convertir UsuarioRemote → Usuario
// -------------------------------------------------------------
fun UsuarioRemote.toLocalModel(): Usuario {
    return Usuario(
        idUsuario = this.idUsuario?.toInt() ?: 0,
        nombreUsuario = this.nombreUsuario ?: "",
        email = this.email ?: "",
        telefono = this.telefono ?: "",
        rol = this.rol ?: "CLIENTE",
        fotoUri = this.foto
    )
}

// -------------------------------------------------------------
//                     FACTORY
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

