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
import com.example.petcareconnect.ui.theme.*
import com.example.petcareconnect.ui.viewmodel.AuthViewModel
import androidx.compose.foundation.BorderStroke

@Composable
fun LoginScreenVm(
    viewModel: AuthViewModel,
    onLoginOkNavigateHome: () -> Unit,
    onGoRegister: () -> Unit
) {
    val state by viewModel.login.collectAsStateWithLifecycle()

    // ⛔ IMPORTANTE: si el login ya fue exitoso, navegar y resetear estado
    if (state.success) {
        viewModel.clearLoginResult()
        onLoginOkNavigateHome()
    }

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
        onSubmit = viewModel::submitLoginRemote,  // ← LOGIN REAL
        onGoRegister = onGoRegister
    )
}


@Composable
private fun LoginScreen(
    email: String,
    pass: String,
    emailError: String?,
    passError: String?,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    errorMsg: String?,
    onEmailChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onGoRegister: () -> Unit
) {
    var showPass by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PetLightBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ----- LOGO -----
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

            // ----- TÍTULO -----
            Text(
                "PetCare Connect",
                style = MaterialTheme.typography.headlineSmall.copy(color = PetGreenPrimary)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "Inicio de Sesión",
                style = MaterialTheme.typography.titleMedium.copy(color = PetDarkGrayText)
            )

            Spacer(Modifier.height(16.dp))


            // -------- EMAIL --------
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Correo electrónico") },
                singleLine = true,
                isError = emailError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            if (emailError != null)
                Text(emailError, color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(8.dp))


            // -------- PASSWORD --------
            OutlinedTextField(
                value = pass,
                onValueChange = onPassChange,
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showPass = !showPass }) {
                        Icon(
                            if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Mostrar/Ocultar contraseña"
                        )
                    }
                },
                isError = passError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (passError != null)
                Text(passError, color = MaterialTheme.colorScheme.error)

            Spacer(Modifier.height(20.dp))


            // -------- BOTÓN LOGIN --------
            Button(
                onClick = onSubmit,
                enabled = canSubmit && !isSubmitting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PetGreenPrimary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Validando...")
                } else {
                    Text("Entrar")
                }
            }


            // -------- ERROR --------
            if (errorMsg != null) {
                Spacer(Modifier.height(8.dp))
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp))


            // -------- BOTÓN REGISTRO --------
            OutlinedButton(
                onClick = onGoRegister,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PetBlueAccent
                ),
                border = BorderStroke(1.dp, PetBlueAccent),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Crear cuenta nueva")
            }
        }
    }
}
