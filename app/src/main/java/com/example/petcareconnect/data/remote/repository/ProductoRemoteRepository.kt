package com.example.petcareconnect.data.remote

import com.example.petcareconnect.data.mapper.toLocal
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.remote.api.ProductoApi
import com.example.petcareconnect.data.remote.dto.CategoriaSimpleDto
import com.example.petcareconnect.data.remote.dto.EstadoRequest
import com.example.petcareconnect.data.remote.dto.ProductoDto
import com.example.petcareconnect.data.remote.dto.ProductoUpdateRequest

class ProductoRemoteRepository(
    private val api: ProductoApi
) {

    // --------------------------------------------------------
    // OBTENER PRODUCTOS (/movil)
    // --------------------------------------------------------
    suspend fun getAllProductosRemotos(): List<Producto> {
        return api.obtenerProductos()
            .map { it.toLocal() }
    }

    // --------------------------------------------------------
    // CREAR PRODUCTO
    // (lo dejamos como estaba porque ya te funciona)
    // --------------------------------------------------------
    suspend fun crearProductoRemoto(
        nombre: String,
        precio: Double,
        stock: Int,
        categoriaId: Long
    ): Producto {

        val categoriaSimpleDto = CategoriaSimpleDto(
            idCategoria = categoriaId,
            nombre = ""   // El backend solo usa el id
        )

        val productoDto = ProductoDto(
            idProducto = null,   // backend lo genera
            nombre = nombre,
            precio = precio,
            stock = stock,
            estado = "DISPONIBLE",
            categoria = categoriaSimpleDto,
            imagenUrl = null
        )

        val creado = api.crearProducto(productoDto)
        return creado.toLocal()
    }

    // --------------------------------------------------------
    // ACTUALIZAR PRODUCTO
    // AHORA COINCIDE CON @PutMapping("/{id}") + ProductoCreateDto
    // --------------------------------------------------------
    suspend fun actualizarProductoRemoto(
        id: Long,
        req: ProductoUpdateRequest
    ): Producto {
        val actualizado = api.actualizarProducto(id, req)
        return actualizado.toLocal()
    }
    // --------------------------------------------------------
    // ELIMINAR
    // --------------------------------------------------------
    suspend fun eliminarProductoRemoto(id: Long) {
        api.eliminarProducto(id)
    }

    // --------------------------------------------------------
    // CAMBIAR ESTADO
    // --------------------------------------------------------
    suspend fun cambiarEstadoRemoto(idProducto: Long, estado: EstadoRequest) {
        api.cambiarEstadoProducto(idProducto, estado)
    }
}
