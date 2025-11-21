package com.example.petcareconnect.data.remote

import com.example.petcareconnect.data.mapper.toLocal
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.remote.api.ProductoApi
import com.example.petcareconnect.data.remote.dto.CategoriaDto
import com.example.petcareconnect.data.remote.dto.EstadoRequest
import com.example.petcareconnect.data.remote.dto.ProductoDto

class ProductoRemoteRepository(
    private val api: ProductoApi
) {

    // --------------------------------------------------------
    // OBTENER PRODUCTOS (usa /movil del Backend)
    // --------------------------------------------------------
    suspend fun getAllProductosRemotos(): List<Producto> {
        return api.obtenerProductos()
            .map { it.toLocal() }
    }

    // --------------------------------------------------------
    // CREAR PRODUCTO (ANDROID)
    // Enviar objeto ProductoDto al backend
    // --------------------------------------------------------
    suspend fun crearProductoRemoto(
        nombre: String,
        precio: Double,
        stock: Int,
        categoriaId: Long
    ): Producto {
        val categoriaDto = CategoriaDto(idCategoria = categoriaId, nombre = "")
        val productoDto = ProductoDto(
            idProducto = 0L, // o null si tu dato lo permite
            nombre = nombre,
            precio = precio,
            stock = stock,
            estado = "DISPONIBLE",
            categoria = categoriaDto,
            imagenUrl = null
        )
        return api.crearProducto(productoDto).toLocal()
    }

    // --------------------------------------------------------
    // ACTUALIZAR PRODUCTO (ANDROID)
    // Usar ProductoDto para actualizar
    // --------------------------------------------------------
    suspend fun actualizarProductoRemoto(producto: ProductoDto): Producto {
        return api.actualizarProducto(producto.idProducto, producto).toLocal()
    }

    // --------------------------------------------------------
    // ELIMINAR PRODUCTO
    // --------------------------------------------------------
    suspend fun eliminarProductoRemoto(id: Long) {
        api.eliminarProducto(id)
    }

    // --------------------------------------------------------
    // CAMBIAR ESTADO (ANDROID)
    // Usa EstadoRequest para enviar estado
    // --------------------------------------------------------
    suspend fun cambiarEstadoRemoto(idProducto: Long, estado: EstadoRequest) {
        api.cambiarEstadoProducto(idProducto, estado)
    }
}
