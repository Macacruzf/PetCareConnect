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

// ---------------------------------------------------------------------------
// AuthViewModel.kt
// ---------------------------------------------------------------------------
// Este ViewModel maneja la autenticación y el registro de usuarios en PetCare Connect.
// Contiene dos flujos principales de estado:
// - LoginUiState: controla el formulario de inicio de sesión.
// - RegisterUiState: controla el formulario de registro.
// Utiliza corrutinas y StateFlow para manejar estados de forma reactiva y segura.
// ---------------------------------------------------------------------------


// ---------------------- ESTADOS DE UI ----------------------

// Estado visual del formulario de inicio de sesión
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

// Estado visual del formulario de registro
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


// ---------------------- USUARIO DEMO ----------------------
// Este modelo interno permite simular un usuario de ejemplo
// cuando no se usa una base de datos real o conexión externa.
private data class DemoUser(
    val name: String,
    val email: String,
    val phone: String,
    val pass: String
)


// ---------------------- VIEWMODEL PRINCIPAL ----------------------
class AuthViewModel(
    private val repository: UsuarioRepository? = null
) : ViewModel() {

    // Lista de usuarios demo para pruebas sin conexión
    companion object {
        private val USERS = mutableListOf(
            DemoUser("Demo", "demo@duoc.cl", "12345678", "Demo123!")
        )
    }

    // Flujos de estado para login y registro
    private val _login = MutableStateFlow(LoginUiState())
    val login: StateFlow<LoginUiState> = _login

    private val _register = MutableStateFlow(RegisterUiState())
    val register: StateFlow<RegisterUiState> = _register

    // Variables globales de sesión: rol actual y usuario autenticado
    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser


    // ---------------------- LOGIN ----------------------

    // Actualiza el campo de correo electrónico y valida su formato
    fun onLoginEmailChange(value: String) {
        _login.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeLoginCanSubmit()
    }

    // Actualiza la contraseña del formulario de inicio de sesión
    fun onLoginPassChange(value: String) {
        _login.update { it.copy(pass = value) }
        recomputeLoginCanSubmit()
    }

    // Habilita o deshabilita el botón de enviar según los campos válidos
    private fun recomputeLoginCanSubmit() {
        val s = _login.value
        val can = s.emailError == null && s.email.isNotBlank() && s.pass.isNotBlank()
        _login.update { it.copy(canSubmit = can) }
    }

    // Envía la solicitud de inicio de sesión
    // Este proceso incluye una animación de carga simulada con delay (efecto visual).
    fun submitLogin() {
        val s = _login.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _login.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            val email = s.email.trim().lowercase()
            val pass = s.pass.trim()

            try {
                // Verifica el usuario en la base de datos o en la lista de prueba
                val dbUser = repository?.login(email, pass)
                val demoUser = USERS.firstOrNull { it.email.equals(email, true) && it.pass == pass }

                val ok = dbUser != null || demoUser != null
                val rol = dbUser?.rol ?: "CLIENTE"

                // Animación: pequeño retraso para simular validación y mostrar el indicador de carga
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

                // Si el login fue exitoso, actualiza los estados globales
                if (ok) {
                    _currentUser.value = dbUser
                    _userRole.value = rol
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

    // Limpia el estado del login después de un intento
    fun clearLoginResult() {
        _login.update { it.copy(success = false, errorMsg = null) }
    }

    // Cierra sesión, restableciendo todos los datos del usuario
    fun logout() {
        _currentUser.value = null
        _userRole.value = null
        _login.value = LoginUiState()
    }


    // ---------------------- REGISTRO ----------------------

    // Actualiza el campo de nombre, permitiendo solo letras y espacios
    fun onNameChange(value: String) {
        val filtered = value.filter { it.isLetter() || it.isWhitespace() }
        _register.update { it.copy(name = filtered, nameError = validateNameLettersOnly(filtered)) }
        recomputeRegisterCanSubmit()
    }

    // Actualiza y valida el correo durante el registro
    fun onRegisterEmailChange(value: String) {
        _register.update { it.copy(email = value, emailError = validateEmail(value)) }
        recomputeRegisterCanSubmit()
    }

    // Actualiza y valida el número telefónico, aceptando solo dígitos
    fun onPhoneChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        _register.update { it.copy(phone = digitsOnly, phoneError = validatePhoneDigitsOnly(digitsOnly)) }
        recomputeRegisterCanSubmit()
    }

    // Actualiza la contraseña, validando fortaleza y coincidencia
    fun onRegisterPassChange(value: String) {
        _register.update { it.copy(pass = value, passError = validateStrongPassword(value)) }
        _register.update { it.copy(confirmError = validateConfirm(it.pass, it.confirm)) }
        recomputeRegisterCanSubmit()
    }

    // Valida que la confirmación coincida con la contraseña
    fun onConfirmChange(value: String) {
        _register.update { it.copy(confirm = value, confirmError = validateConfirm(it.pass, value)) }
        recomputeRegisterCanSubmit()
    }

    // Guarda la ruta o URI de la imagen del usuario
    fun onFotoSelected(uri: String) {
        _register.update { it.copy(fotoUri = uri) }
    }

    // Cambia el rol del usuario antes del registro
    fun onRolChange(newRol: String) {
        _register.update { it.copy(rol = newRol) }
    }

    // Habilita el botón de registro solo si todos los campos son válidos
    private fun recomputeRegisterCanSubmit() {
        val s = _register.value
        val noErrors = listOf(s.nameError, s.emailError, s.phoneError, s.passError, s.confirmError).all { it == null }
        val filled = s.name.isNotBlank() && s.email.isNotBlank() &&
                s.phone.isNotBlank() && s.pass.isNotBlank() && s.confirm.isNotBlank()
        _register.update { it.copy(canSubmit = noErrors && filled) }
    }

    // Envía los datos del nuevo usuario
    // Incluye una animación de espera (delay) para simular procesamiento y retroalimentar visualmente al usuario.
    fun submitRegister() {
        val s = _register.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            _register.update { it.copy(isSubmitting = true, errorMsg = null, success = false) }

            val name = s.name.trim()
            val email = s.email.trim().lowercase()
            val phone = s.phone.trim()
            val pass = s.pass.trim()
            val fotoUri = s.fotoUri
            val rol = s.rol ?: "CLIENTE"

            try {
                // Verifica si el correo ya existe
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

                // Inserta el nuevo usuario en la base de datos
                repository?.insert(
                    Usuario(
                        nombreUsuario = name,
                        email = email,
                        telefono = phone,
                        password = pass,
                        rol = rol,
                        fotoUri = fotoUri
                    )
                )

                // Lo agrega también a la lista de prueba (modo demo)
                USERS.add(DemoUser(name, email, phone, pass))

                // Animación: breve retraso que simula proceso de guardado
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

    // Limpia los resultados del registro luego de una operación
    fun clearRegisterResult() {
        _register.update { it.copy(success = false, errorMsg = null) }
    }
}


// ---------------------- FACTORY ----------------------
// Clase auxiliar que permite crear una instancia de AuthViewModel
// con el repositorio inyectado (para manejar dependencias de forma segura).
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
