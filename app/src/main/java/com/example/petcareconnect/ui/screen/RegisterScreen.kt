package com.example.petcareconnect.ui.screen

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import coil.compose.rememberAsyncImagePainter
import com.example.petcareconnect.R
import com.example.petcareconnect.data.db.PetCareDatabase
import com.example.petcareconnect.data.repository.UsuarioRepository
import com.example.petcareconnect.ui.viewmodel.AuthViewModel
import com.example.petcareconnect.ui.viewmodel.AuthViewModelFactory
import java.io.File
import java.io.FileOutputStream
import androidx.core.content.FileProvider

// -------------------------------------------------------------
// 🌟 VIEWMODEL WRAPPER
// -------------------------------------------------------------
@Composable
fun RegisterScreenVm(
    onRegisteredNavigateLogin: () -> Unit,
    onGoLogin: () -> Unit
) {
    val context = LocalContext.current
    val db = remember {
        Room.databaseBuilder(
            context,
            PetCareDatabase::class.java,
            "petcare_db"
        ).build()
    }
    val repository = remember { UsuarioRepository(db.usuarioDao()) }
    val vm: AuthViewModel = viewModel(factory = AuthViewModelFactory(repository))
    val state by vm.register.collectAsStateWithLifecycle()

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

// -------------------------------------------------------------
// 🌟 REGISTER SCREEN UI
// -------------------------------------------------------------
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
            Icon(
                painter = painterResource(id = R.drawable.ic_petcare_logo),
                contentDescription = "Logo PetCare Connect",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            )
            Spacer(Modifier.height(12.dp))

            Text("Crea tu cuenta", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Regístrate para comenzar a usar PetCare Connect",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF333333)
            )
            Spacer(Modifier.height(20.dp))

            // 📸 Foto de perfil
            FotoSelector(onImageSelected = onFotoSelected)
            AnimatedVisibility(fotoUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(fotoUri),
                    contentDescription = "Vista previa de foto",
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .size(100.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(Modifier.height(20.dp))

            // 🔹 Campos de texto
            RegisterField(label = "Nombre completo", value = name, onChange = onNameChange, error = nameError)
            RegisterField(label = "Correo electrónico", value = email, onChange = onEmailChange, error = emailError, type = KeyboardType.Email)
            RegisterField(label = "Teléfono", value = phone, onChange = onPhoneChange, error = phoneError, type = KeyboardType.Number)

            PasswordField("Contraseña", pass, onPassChange, passError, showPass) { showPass = !showPass }
            PasswordField("Confirmar contraseña", confirm, onConfirmChange, confirmError, showConfirm) { showConfirm = !showConfirm }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onSubmit,
                enabled = canSubmit && !isSubmitting,
                modifier = Modifier.fillMaxWidth().height(50.dp)
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


// 🧩 CAMPOS REUTILIZABLES
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
    error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }
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
                Icon(if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
    error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }
    Spacer(Modifier.height(8.dp))
}

// -------------------------------------------------------------
// 📷 FOTO SELECTOR COMPONENT
// -------------------------------------------------------------
@Composable
fun FotoSelector(onImageSelected: (String) -> Unit) {
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            val uri = saveBitmapToCache(context, it)
            onImageSelected(uri.toString())
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onImageSelected(it.toString()) }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(onClick = { cameraLauncher.launch(null) }) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Cámara")
            Spacer(Modifier.width(6.dp))
            Text("Cámara")
        }
        OutlinedButton(onClick = { galleryLauncher.launch("image/*") }) {
            Icon(Icons.Default.Image, contentDescription = "Galería")
            Spacer(Modifier.width(6.dp))
            Text("Galería")
        }
    }
}

// -------------------------------------------------------------
// 💾 GUARDAR FOTO TEMPORALMENTE
// -------------------------------------------------------------
fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "foto_${System.currentTimeMillis()}.png")
    FileOutputStream(file).use {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
    }
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}
