package com.example.petcareconnect.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.petcareconnect.data.model.Usuario
import com.example.petcareconnect.data.repository.UsuarioRepository
import com.example.petcareconnect.domain.validation.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ----------------- ESTADOS DE UI -----------------
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

// ----------------- USUARIO DEMO -----------------
private data class DemoUser(
    val name: String,
    val email: String,
    val phone: String,
    val pass: String
)

// ----------------- VIEWMODEL -----------------
class AuthViewModel(
    private val repository: UsuarioRepository? = null
) : ViewModel() {

    companion object {
        private val USERS = mutableListOf(
            DemoUser("Demo", "demo@duoc.cl", "12345678", "Demo123!")
        )
    }

    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    // ----------------- LOGIN -----------------
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
        val can = s.emailError == null && s.email.isNotBlank() && s.pass.isNotBlank()
        _login.update { it.copy(canSubmit = can) }
    }

    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            val email = s.email.trim().lowercase()
            val pass = s.pass.trim()

            try {
                val dbUser = repository?.login(email, pass)
                val demoUser = USERS.firstOrNull { it.email.equals(email, true) && it.pass == pass }

                val ok = dbUser != null || demoUser != null
                val rol = dbUser?.rol ?: "CLIENTE"

                delay(400)
                _login.update {
                    it.copy(
                        isSubmitting = false,
                        success = ok,
                        errorMsg = if (!ok) "Credenciales inválidas" else null,
                        rol = rol,
                        usuario = dbUser
                    )
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

    fun clearLoginResult() {
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    // ----------------- REGISTRO -----------------
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
        val digitsOnly = value.filter { it.isDigit() }
        _register.update { it.copy(phone = digitsOnly, phoneError = validatePhoneDigitsOnly(digitsOnly)) }
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

    // ✅ Nuevo: guardar foto seleccionada
    fun onFotoSelected(uri: String) {
        _register.update { it.copy(fotoUri = uri) }
    }

    private fun recomputeRegisterCanSubmit() {
        val s = _register.value
        val noErrors = listOf(s.nameError, s.emailError, s.phoneError, s.passError, s.confirmError).all { it == null }
        val filled = s.name.isNotBlank() && s.email.isNotBlank() &&
                s.phone.isNotBlank() && s.pass.isNotBlank() && s.confirm.isNotBlank()
        _register.update { it.copy(canSubmit = noErrors && filled) }
    }

    fun submitRegister() {
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            val name = s.name.trim()
            val email = s.email.trim().lowercase()
            val phone = s.phone.trim()
            val pass = s.pass.trim()
            val fotoUri = s.fotoUri // ✅ se guarda la foto

            try {
                val existingUser = repository?.getByEmail(email)
                if (existingUser != null) {
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
                        nombre = name,
                        email = email,
                        telefono = phone,
                        password = pass,
                        rol = "CLIENTE",
                        fotoUri = fotoUri // ✅ guardamos la foto en la BD
                    )
                )

                USERS.add(DemoUser(name, email, phone, pass))

                delay(400)
                _register.update {
                    it.copy(isSubmitting = false, success = true, errorMsg = null)
                }
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
}

// ----------------- FACTORY -----------------
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
