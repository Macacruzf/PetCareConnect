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
import com.example.petcareconnect.data.model.Carrito
import com.example.petcareconnect.ui.viewmodel.CarritoViewModel

@Composable
fun CarritoScreen(
    viewModel: CarritoViewModel,
    onConfirmarCompra: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "🛒 Mi Carrito",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF2196F3)
        )

        Spacer(Modifier.height(12.dp))

        if (state.items.isEmpty()) {
            Text("Tu carrito está vacío.", color = Color.Gray)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(state.items) { item ->
                    CarritoItemCard(item, onEliminar = { viewModel.eliminarItem(item.idItem) }) {
                        viewModel.actualizarCantidad(item, it)
                    }
                }
            }

            Divider(Modifier.padding(vertical = 8.dp))

            Text(
                "Total: $${String.format("%.2f", state.total)}",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF4CAF50)
            )

            Spacer(Modifier.height(16.dp))

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

@Composable
fun CarritoItemCard(
    item: Carrito,
    onEliminar: () -> Unit,
    onCantidadChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item.imagenResId?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = item.nombre,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.width(10.dp))

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

            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
            }
        }
    }
}

@Composable
fun QuantitySelector(
    cantidad: Int,
    onCantidadChange: (Int) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedButton(
            onClick = { if (cantidad > 1) onCantidadChange(cantidad - 1) },
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) { Text("-") }

        Text(cantidad.toString(), Modifier.padding(horizontal = 8.dp))

        OutlinedButton(
            onClick = { onCantidadChange(cantidad + 1) },
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) { Text("+") }
    }
}

