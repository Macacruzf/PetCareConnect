package com.example.petcareconnect.ui.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.petcareconnect.ui.viewmodel.AuthViewModel
import java.io.File
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// -----------------------------------------------------------
// RegisterScreenVm: versión con ViewModel conectado
// -----------------------------------------------------------
@Composable
fun RegisterScreenVm(
    vm: AuthViewModel,
    onRegisteredNavigateLogin: () -> Unit,
    onGoLogin: () -> Unit
) {
    val state by vm.register.collectAsStateWithLifecycle()

    // Siempre se fuerza el rol CLIENTE
    LaunchedEffect(Unit) {
        vm.onRolChange("CLIENTE")
    }

    if (state.success) {
        vm.clearRegisterResult()
        onRegisteredNavigateLogin()
    }

    RegisterScreen(
        name = state.name,
        email = state.email,
        phone = state.phone,
        pass = state.pass,
        confirm = state.confirm,
        fotoUri = state.fotoUri,
        nameError = state.nameError,
        emailError = state.emailError,
        phoneError = state.phoneError,
        passError = state.passError,
        confirmError = state.confirmError,
        canSubmit = state.canSubmit,
        isSubmitting = state.isSubmitting,
        errorMsg = state.errorMsg,
        onNameChange = vm::onNameChange,
        onEmailChange = vm::onRegisterEmailChange,
        onPhoneChange = vm::onPhoneChange,
        onPassChange = vm::onRegisterPassChange,
        onConfirmChange = vm::onConfirmChange,
        onFotoSelected = vm::onFotoSelected,
        onSubmit = vm::submitRegister,
        onGoLogin = onGoLogin
    )
}

// -----------------------------------------------------------
// RegisterScreen: pantalla de registro sin campo de Rol
// -----------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegisterScreen(
    name: String,
    email: String,
    phone: String,
    pass: String,
    confirm: String,
    fotoUri: String?,
    nameError: String?,
    emailError: String?,
    phoneError: String?,
    passError: String?,
    confirmError: String?,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    errorMsg: String?,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPassChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onFotoSelected: (String) -> Unit,
    onSubmit: () -> Unit,
    onGoLogin: () -> Unit
) {
    var showPass by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(fotoUri?.let { Uri.parse(it) }) }
    val photoUri = remember { mutableStateOf<Uri?>(null) }

    // 🔹 Cámara real (TakePicture)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri.value != null) {
            imageUri = photoUri.value
            onFotoSelected(photoUri.value.toString())
        }
    }

    // 🔹 Permiso de cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val file = File(context.cacheDir, "foto_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            photoUri.value = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // 🔹 Galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            onFotoSelected(it.toString())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 400.dp)
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // -----------------------------------------------------------
            // FOTO DE PERFIL
            // -----------------------------------------------------------
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0E0E0))
                    .clickable { showMenu = true },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Agregar foto",
                        tint = Color.Gray,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            // -----------------------------------------------------------
            // MENÚ (CÁMARA / GALERÍA)
            // -----------------------------------------------------------
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(
                    text = { Text("Tomar foto") },
                    leadingIcon = { Icon(Icons.Default.CameraAlt, contentDescription = null) },
                    onClick = {
                        showMenu = false
                        cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                )
                DropdownMenuItem(
                    text = { Text("Elegir desde galería") },
                    leadingIcon = { Icon(Icons.Default.Image, contentDescription = null) },
                    onClick = {
                        showMenu = false
                        galleryLauncher.launch("image/*")
                    }
                )
            }

            Spacer(Modifier.height(20.dp))

            // -----------------------------------------------------------
            // CAMPOS DEL FORMULARIO
            // -----------------------------------------------------------
            RegisterField("Nombre completo", name, onNameChange, nameError)
            RegisterField("Correo electrónico", email, onEmailChange, emailError, KeyboardType.Email)
            RegisterField("Teléfono", phone, onPhoneChange, phoneError, KeyboardType.Number)

            PasswordField("Contraseña", pass, onPassChange, passError, showPass) { showPass = !showPass }
            PasswordField("Confirmar contraseña", confirm, onConfirmChange, confirmError, showConfirm) { showConfirm = !showConfirm }

            Spacer(Modifier.height(20.dp))

            // -----------------------------------------------------------
            // BOTÓN REGISTRARSE
            // -----------------------------------------------------------
            Button(
                onClick = onSubmit,
                enabled = canSubmit && !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Creando cuenta...")
                } else {
                    Text("Registrarse")
                }
            }

            errorMsg?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onGoLogin, modifier = Modifier.fillMaxWidth()) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}

// -----------------------------------------------------------
// Campos reutilizables
// -----------------------------------------------------------
@Composable
fun RegisterField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    error: String?,
    type: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        isError = error != null,
        keyboardOptions = KeyboardOptions(keyboardType = type),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
    error?.let {
        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
    }
    Spacer(Modifier.height(8.dp))
}

@Composable
fun PasswordField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    error: String?,
    visible: Boolean,
    onToggle: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        isError = error != null,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = onToggle) {
                Icon(
                    if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
    error?.let {
        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
    }
    Spacer(Modifier.height(8.dp))
}
