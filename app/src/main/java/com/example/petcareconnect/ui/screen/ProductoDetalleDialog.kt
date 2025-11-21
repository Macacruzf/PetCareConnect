package com.example.petcareconnect.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource              // ← IMPORT NECESARIO
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.petcareconnect.R
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.model.EstadoProducto

@Composable
fun ProductoDetalleDialog(
    producto: Producto,
    esAdmin: Boolean,

    onCambiarEstado: (EstadoProducto) -> Unit,
    onAgregar: (Producto) -> Unit,
    onEliminar: (Producto) -> Unit,
    onCerrar: () -> Unit,
    onEditar: (Producto) -> Unit
) {

    Dialog(onDismissRequest = onCerrar) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {

            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {

                // ---------- CERRAR ----------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onCerrar) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                // ---------- IMAGEN ----------
                Image(
                    painter = painterResource(id = R.drawable.ic_petcare_logo),
                    contentDescription = "Imagen Producto",
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.height(10.dp))

                // ---------- NOMBRE ----------
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // ---------- PRECIO ----------
                Text(
                    text = "$${producto.precio}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(10.dp))

                // ---------- BADGE DE ESTADO ----------
                EstadoBadge(producto.estado)

                Spacer(Modifier.height(16.dp))

                // ---------- CLIENTE: AGREGAR AL CARRITO ----------
                val puedeAgregar =
                    producto.estado == EstadoProducto.DISPONIBLE

                Button(
                    onClick = { if (puedeAgregar) onAgregar(producto) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = puedeAgregar
                ) {
                    Icon(Icons.Default.ShoppingCart, null)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        when (producto.estado) {
                            EstadoProducto.SIN_STOCK -> "Sin stock"
                            EstadoProducto.NO_DISPONIBLE -> "No disponible"
                            else -> "Agregar al carrito"
                        }
                    )
                }

                // ---------- ADMIN ----------
                if (esAdmin) {

                    Spacer(Modifier.height(14.dp))

                    // EDITAR
                    OutlinedButton(
                        onClick = { onEditar(producto) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Edit, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Editar producto")
                    }

                    Spacer(Modifier.height(10.dp))

                    // ELIMINAR
                    OutlinedButton(
                        onClick = { onEliminar(producto) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Delete, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Eliminar")
                    }

                    Spacer(Modifier.height(10.dp))

                    // CAMBIAR ESTADO (ACTIVAR/DESACTIVAR)
                    val nuevoEstado =
                        if (producto.estado == EstadoProducto.DISPONIBLE)
                            EstadoProducto.NO_DISPONIBLE
                        else
                            EstadoProducto.DISPONIBLE

                    Button(
                        onClick = { onCambiarEstado(nuevoEstado) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (producto.estado == EstadoProducto.DISPONIBLE)
                                "Desactivar"
                            else
                                "Activar"
                        )
                    }
                }
            }
        }
    }
}

/* -------------------------------------------------------------
   BADGE VISUAL PARA ESTADOS
------------------------------------------------------------- */

@Composable
fun EstadoBadge(estado: EstadoProducto) {

    val (text, color) = when (estado) {

        EstadoProducto.DISPONIBLE ->
            "DISPONIBLE" to Color(0xFF4CAF50) // Verde

        EstadoProducto.SIN_STOCK ->
            "SIN STOCK" to Color(0xFFFF3B3B) // Rojo

        EstadoProducto.NO_DISPONIBLE ->
            "NO DISPONIBLE" to Color(0xFFFF9800) // Naranja

        else -> "Estado" to Color.Gray
    }

    // Centramos el badge sin usar align() en el Modifier
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(color.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = text,
                color = color,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
