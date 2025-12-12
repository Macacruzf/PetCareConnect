package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.petcareconnect.data.model.Carrito
import com.example.petcareconnect.ui.viewmodel.CarritoViewModel

/*
 * Pantalla del carrito de compras.
 * Permite visualizar los productos añadidos, modificar cantidades,
 * eliminar ítems y confirmar la compra.
 */
@Composable
fun CarritoScreen(
    viewModel: CarritoViewModel,       // ViewModel encargado de la lógica del carrito
    onConfirmarCompra: () -> Unit      // Acción que se ejecuta al presionar “Confirmar compra”
) {
    // Observa el estado del carrito (lista de productos y total acumulado)
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título principal de la pantalla
        Text(
            "Mi Carrito",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF2196F3) // Azul corporativo
        )

        Spacer(Modifier.height(12.dp))

        // Si no hay productos en el carrito, muestra un mensaje informativo
        if (state.items.isEmpty()) {
            Text("Tu carrito está vacío.", color = Color.Gray)
        } else {
            // Lista con desplazamiento vertical de productos en el carrito
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(state.items) { item ->
                    // Renderiza cada producto mediante una tarjeta individual
                    CarritoItemCard(
                        item = item,
                        onEliminar = { viewModel.eliminarItem(item.idItem) },
                        onCantidadChange = { nuevaCantidad ->
                            viewModel.actualizarCantidad(item, nuevaCantidad)
                        }
                    )
                }
            }

            // Línea divisoria entre la lista y el total
            Divider(Modifier.padding(vertical = 8.dp))

            // Total acumulado del carrito
            Text(
                "Total: $${String.format("%.2f", state.total)}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF4CAF50)
            )

            Spacer(Modifier.height(16.dp))

            // Botón para confirmar la compra
            Button(
                onClick = onConfirmarCompra,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Confirmar compra", color = Color.White)
            }
        }
    }
}

/*
 * Representa visualmente cada producto dentro del carrito.
 * Muestra su imagen, nombre, precio, cantidad y un botón para eliminarlo.
 */
@Composable
fun CarritoItemCard(
    item: Carrito,                     // Producto del carrito
    onEliminar: () -> Unit,            // Acción al presionar el ícono de eliminar
    onCantidadChange: (Int) -> Unit    // Acción al modificar la cantidad
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(3.dp) // Sombra sutil para destacar la tarjeta
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ Mostrar imagen con prioridad: imagenUrl (backend) > imagenUri (local) > imagenResId (drawable)
            when {
                item.imagenUrl != null -> {
                    Image(
                        painter = rememberAsyncImagePainter(item.imagenUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(MaterialTheme.shapes.small),
                        contentScale = ContentScale.Crop
                    )
                }

                item.imagenUri != null -> {
                    Image(
                        painter = rememberAsyncImagePainter(item.imagenUri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(MaterialTheme.shapes.small),
                        contentScale = ContentScale.Crop
                    )
                }

                item.imagenResId != null -> {
                    Image(
                        painter = painterResource(id = item.imagenResId),
                        contentDescription = null,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(MaterialTheme.shapes.small),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(Modifier.width(10.dp))

            // Información del producto (nombre, precio, cantidad)
            Column(modifier = Modifier.weight(1f)) {
                Text(item.nombre, fontWeight = FontWeight.Bold)
                Text("Precio: $${item.precio}")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Cantidad: ")
                    QuantitySelector(
                        cantidad = item.cantidad,
                        onCantidadChange = onCantidadChange
                    )
                }
            }

            // Botón de eliminación del producto
            IconButton(onClick = onEliminar) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.Red
                )
            }
        }
    }
}

/*
 * Selector de cantidad con botones "+" y "-" para ajustar el número de unidades de un producto.
 */
@Composable
fun QuantitySelector(
    cantidad: Int,                     // Cantidad actual del producto
    onCantidadChange: (Int) -> Unit    // Acción a ejecutar al cambiar la cantidad
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Botón para disminuir cantidad (mínimo: 1)
        OutlinedButton(
            onClick = { if (cantidad > 1) onCantidadChange(cantidad - 1) },
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) { Text("-") }

        // Texto central con la cantidad actual
        Text(cantidad.toString(), Modifier.padding(horizontal = 8.dp))

        // Botón para aumentar cantidad
        OutlinedButton(
            onClick = { onCantidadChange(cantidad + 1) },
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) { Text("+") }
    }
}
