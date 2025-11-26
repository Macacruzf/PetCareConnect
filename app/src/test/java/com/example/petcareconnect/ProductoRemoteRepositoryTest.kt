package com.example.petcareconnect

import com.example.petcareconnect.data.remote.ProductoRemoteRepository
import com.example.petcareconnect.data.remote.api.ProductoApi
import com.example.petcareconnect.data.remote.dto.ProductoDto
import com.example.petcareconnect.data.remote.dto.CategoriaSimpleDto
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class ProductoRemoteRepositoryTest {

    private lateinit var productoApi: ProductoApi
    private lateinit var repository: ProductoRemoteRepository

    @Before
    fun setup() {
        productoApi = mockk()
        repository = ProductoRemoteRepository(productoApi)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getAllProductosRemotos mapea DTOs a locales`() = runTest {
        // Given
        val dto = ProductoDto(
            idProducto = 1,
            nombre = "Test",
            precio = 100.0,
            stock = 10,
            estado = "DISPONIBLE",
            categoria = CategoriaSimpleDto(idCategoria = 1, nombre = "Test")
        )
        coEvery { productoApi.obtenerProductos() } returns listOf(dto)

        // When
        val result = repository.getAllProductosRemotos()

        // Then
        assertEquals(1, result.size)
        assertEquals(1, result[0].idProducto)
        assertEquals("Test", result[0].nombre)
        coVerify(exactly = 1) { productoApi.obtenerProductos() }
    }

    @Test
    fun `actualizarProductoRemoto envia body y mapea respuesta`() = runTest {
        // Given
        val dto = ProductoDto(
            idProducto = 1,
            nombre = "Updated",
            precio = 250.0,
            stock = 20,
            estado = "DISPONIBLE",
            categoria = CategoriaSimpleDto(idCategoria = 1, nombre = "Test"),
            imagenUrl = null
        )
        val request = mockk<com.example.petcareconnect.data.remote.dto.ProductoUpdateRequest>()
        coEvery { productoApi.actualizarProducto(1L, request) } returns dto

        // When
        val result = repository.actualizarProductoRemoto(1L, request)

        // Then
        assertEquals("Updated", result.nombre)
        coVerify(exactly = 1) { productoApi.actualizarProducto(1L, request) }
    }

    @Test
    fun `eliminarProductoRemoto llama a API con id correcto`() = runTest {
        // Given
        coEvery { productoApi.eliminarProducto(1L) } just Runs

        // When
        repository.eliminarProductoRemoto(1)

        // Then
        coVerify(exactly = 1) { productoApi.eliminarProducto(1L) }
    }

    @Test
    fun `cambiarEstadoRemoto envia estado correcto`() = runTest {
        // Given
        val estadoRequest = mockk<com.example.petcareconnect.data.remote.dto.EstadoRequest>()
        val response = mapOf("message" to "Estado actualizado", "success" to true)
        coEvery { productoApi.cambiarEstadoProducto(1L, estadoRequest) } returns response

        // When
        repository.cambiarEstadoRemoto(1L, estadoRequest)

        // Then
        coVerify(exactly = 1) { productoApi.cambiarEstadoProducto(1L, estadoRequest) }
    }
}