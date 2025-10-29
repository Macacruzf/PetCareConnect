package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.petcareconnect.R
import com.example.petcareconnect.ui.viewmodel.AuthViewModel

/*
 * Pantalla de inicio de sesión que se conecta directamente con AuthViewModel.
 * Controla los estados de autenticación y redirige al usuario cuando el login es exitoso.
 */
@Composable
fun LoginScreenVm(
    viewModel: AuthViewModel,                // ViewModel que gestiona la lógica de autenticación
    onLoginOkNavigateHome: () -> Unit,       // Acción a ejecutar tras un inicio de sesión exitoso
    onGoRegister: () -> Unit                 // Acción para navegar a la pantalla de registro
) {
    // Se observa el estado del proceso de login con recolección del ciclo de vida
    val state by viewModel.login.collectAsStateWithLifecycle()

    // Si el login fue exitoso, se limpia el estado y se navega a la pantalla principal
    if (state.success) {
        viewModel.clearLoginResult()
        onLoginOkNavigateHome()
    }

    // Renderizado del formulario de login
    LoginScreen(
        email = state.email,
        pass = state.pass,
        emailError = state.emailError,
        passError = state.passError,
        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        errorMsg = state.errorMsg,
        onEmailChange = viewModel::onLoginEmailChange,
        onPassChange = viewModel::onLoginPassChange,
        onSubmit = viewModel::submitLogin,
        onGoRegister = onGoRegister
    )
}

/*
 * Componente visual del formulario de inicio de sesión.
 * Separa la lógica del ViewModel de la interfaz de usuario.
 */
@Composable
private fun LoginScreen(
    email: String,                       // Valor actual del campo de correo
    pass: String,                        // Valor actual del campo de contraseña
    emailError: String?,                 // Error asociado al correo (si existe)
    passError: String?,                  // Error asociado a la contraseña (si existe)
    canSubmit: Boolean,                  // Indica si el formulario es válido para enviar
    isSubmitting: Boolean,               // Indica si se está procesando el login
    errorMsg: String?,                   // Mensaje de error general (por ejemplo, credenciales inválidas)
    onEmailChange: (String) -> Unit,     // Acción al modificar el campo de correo
    onPassChange: (String) -> Unit,      // Acción al modificar el campo de contraseña
    onSubmit: () -> Unit,                // Acción al presionar el botón de login
    onGoRegister: () -> Unit             // Acción para crear una nueva cuenta
) {
    // Estado local que controla la visibilidad de la contraseña
    var showPass by remember { mutableStateOf(false) }

    // Contenedor principal centrado
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9)) // Fondo gris muy claro
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // Columna con todos los elementos del formulario
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo de la aplicación
            Image(
                painter = painterResource(id = R.drawable.ic_petcare_logo),
                contentDescription = "Logo PetCare Connect",
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(12.dp))

            // Nombre de la aplicación
            Text(
                "PetCare Connect",
                style = MaterialTheme.typography.headlineSmall.copy(color = Color(0xFF4CAF50))
            )

            Spacer(Modifier.height(24.dp))

            Text("Inicio de Sesión", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))

            // Campo de texto para el correo electrónico
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Correo electrónico") },
                singleLine = true,
                isError = emailError != null, // Se marca como error si existe mensaje
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            // Mensaje de error debajo del campo
            if (emailError != null)
                Text(emailError, color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(8.dp))

            // Campo de texto para la contraseña con opción de mostrar/ocultar
            OutlinedTextField(
                value = pass,
                onValueChange = onPassChange,
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            imageVector = if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Mostrar u ocultar contraseña"
                        )
                    }
                },
                isError = passError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (passError != null)
                Text(passError, color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(16.dp))

            // Botón principal de inicio de sesión
            Button(
                onClick = onSubmit,
                enabled = canSubmit && !isSubmitting, // Solo habilitado si los campos son válidos
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                // Mientras se valida, se muestra un indicador de progreso
                if (isSubmitting) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Validando...")
                } else {
                    Text("Entrar")
                }
            }

            // Mensaje de error general (por ejemplo, credenciales inválidas)
            if (errorMsg != null) {
                Spacer(Modifier.height(8.dp))
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp))

            // Botón para crear una nueva cuenta
            OutlinedButton(
                onClick = onGoRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Crear cuenta nueva")
            }
        }
    }
}
