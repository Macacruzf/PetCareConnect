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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.petcareconnect.data.model.EstadoProducto
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

    LaunchedEffect(Unit) {
        productoViewModel.cargarProductoParaEdicion(producto)
    }

    val scrollState = rememberScrollState()

    // -------- Imagen --------
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

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
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                photoUriState.value = uri
                cameraLauncher.launch(uri)
            } else {
                Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }

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

        // ===== BottomSheet =====
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

        // ------------------------------------------------------------
        //       CONTENIDO + BOTÓN FIJO
        // ------------------------------------------------------------
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .padding(horizontal = 18.dp)
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
                    .padding(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Text(
                    "Actualiza la información del producto",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                // Imagen
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

                // Nombre
                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = productoViewModel::onNombreChange,
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Precio
                OutlinedTextField(
                    value = state.precio,
                    onValueChange = {
                        if (it.all { ch -> ch.isDigit() || ch == '.' }) {
                            productoViewModel.onPrecioChange(it)
                        }
                    },
                    label = { Text("Precio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Stock
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

                // Categoría
                var expandedCat by remember { mutableStateOf(false) }
                val categoriaActual = state.categorias.firstOrNull {
                    it.idCategoria == state.categoriaId
                }

                ExposedDropdownMenuBox(
                    expanded = expandedCat,
                    onExpandedChange = { expandedCat = !expandedCat }
                ) {
                    OutlinedTextField(
                        value = categoriaActual?.nombre ?: "Seleccionar categoría",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCat,
                        onDismissRequest = { expandedCat = false }
                    ) {
                        state.categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria.nombre) },
                                onClick = {
                                    productoViewModel.onCategoriaChange(categoria.idCategoria)
                                    expandedCat = false
                                }
                            )
                        }
                    }
                }

                // Estado
                Text(
                    "Estado del producto",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = {
                        productoViewModel.onEstadoChange(EstadoProducto.DISPONIBLE)
                    }) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50))
                    }

                    IconButton(onClick = {
                        productoViewModel.onEstadoChange(EstadoProducto.NO_DISPONIBLE)
                    }) {
                        Icon(Icons.Default.Block, null, tint = Color(0xFF616161))
                    }

                    IconButton(onClick = {
                        productoViewModel.onEstadoChange(EstadoProducto.SIN_STOCK)
                    }) {
                        Icon(Icons.Default.Warning, null, tint = Color(0xFFFF9800))
                    }
                }

                Text("Estado actual: ${state.estado.name}")
            }

            // ----------------------------------------------------
            //            BOTÓN FIJO — YA SIN MORADO
            // ----------------------------------------------------
            Button(
                onClick = {
                    productoViewModel.editarProducto()
                    onVolver()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(18.dp)
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3),   // 🔵 Azul acento
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar cambios", fontWeight = FontWeight.Bold)
            }
        }
    }
}
