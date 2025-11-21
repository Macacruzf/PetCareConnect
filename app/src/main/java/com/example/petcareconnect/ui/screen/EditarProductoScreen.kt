package com.example.petcareconnect.ui.screen

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.ui.viewmodel.ProductoViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProductoScreen(
    productoViewModel: ProductoViewModel,
    producto: Producto,
    onVolver: () -> Unit
) {
    val state by productoViewModel.state.collectAsState()
    val context = LocalContext.current

    // Cargar datos al abrir
    LaunchedEffect(producto.idProducto) {
        productoViewModel.cargarProductoParaEdicion(producto)
    }

    // BottomSheet
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    // Cámara
    val photoUriState = remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && photoUriState.value != null) {
                productoViewModel.onImagenUriChange(photoUriState.value.toString())
            }
        }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                val file = File(context.cacheDir, "producto_edit_${System.currentTimeMillis()}.jpg")
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                photoUriState.value = uri
                cameraLauncher.launch(uri)
            } else {
                Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }

    // Galería
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            productoViewModel.onImagenUriChange(uri?.toString())
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar producto") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text("Seleccionar imagen", fontWeight = FontWeight.Bold)

                    Spacer(Modifier.height(18.dp))

                    OutlinedButton(
                        onClick = {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            showSheet = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoCamera, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Cámara")
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            galleryLauncher.launch("image/*")
                            showSheet = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Image, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Galería")
                    }

                    Spacer(Modifier.height(25.dp))
                }
            }
        }

        // CONTENIDO
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(18.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Text(
                "Actualiza la información del producto",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            // ===== IMAGEN =====
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE0E0E0))
                    .align(Alignment.CenterHorizontally)
                    .clickable { showSheet = true },
                contentAlignment = Alignment.Center
            ) {
                if (state.imagenUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(state.imagenUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Toca para cambiar imagen")
                }
            }

            // ===== NOMBRE =====
            OutlinedTextField(
                value = state.nombre,
                onValueChange = productoViewModel::onNombreChange,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            // ===== PRECIO =====
            OutlinedTextField(
                value = state.precio,
                onValueChange = {
                    if (it.all { ch -> ch.isDigit() || ch == '.' })
                        productoViewModel.onPrecioChange(it)
                },
                label = { Text("Precio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // ===== STOCK =====
            OutlinedTextField(
                value = state.stock,
                onValueChange = {
                    if (it.all { ch -> ch.isDigit() })
                        productoViewModel.onStockChange(it)
                },
                label = { Text("Stock") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // ===== CATEGORIA =====
            OutlinedTextField(
                value = state.categorias.firstOrNull {
                    it.idCategoria == state.categoriaId
                }?.nombre ?: "",
                onValueChange = {},
                label = { Text("Categoría") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            // ===== ESTADO =====
            OutlinedTextField(
                value = state.estado.name,
                onValueChange = {},
                label = { Text("Estado") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(15.dp))

            Button(
                onClick = {
                    productoViewModel.editarProducto()
                    onVolver()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text("Guardar cambios", fontWeight = FontWeight.Bold)
            }
        }
    }
}
