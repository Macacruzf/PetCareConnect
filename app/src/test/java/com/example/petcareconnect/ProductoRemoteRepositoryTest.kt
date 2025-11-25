package com.example.petcareconnect

import com.example.petcareconnect.data.model.EstadoProducto
import com.example.petcareconnect.data.remote.ProductoRemoteRepository
import com.example.petcareconnect.data.remote.api.ProductoApi
import com.example.petcareconnect.data.remote.dto.ProductoDto
import com.example.petcareconnect.data.remote.dto.CategoriaSimpleDto
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class ProductoRemoteRepositoryTest {

    @Mock
    private lateinit var productoApi: ProductoApi

    private lateinit var repository: ProductoRemoteRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = ProductoRemoteRepository(productoApi)
    }

    @Test
    fun `getAllProductosRemotos mapea DTOs a locales`() = runTest {
        val dto = ProductoDto(
            idProducto = 1,
            nombre = "Test",
            precio = 100.0,
            stock = 10,
            estado = "DISPONIBLE",
            categoria = CategoriaSimpleDto(idCategoria = 1, nombre = "Test")
        )
        `when`(productoApi.obtenerProductos()).thenReturn(listOf(dto))

        val result = repository.getAllProductosRemotos()

        assertThat(result).hasSize(1)
        assertThat(result[0].idProducto).isEqualTo(1)
        assertThat(result[0].nombre).isEqualTo("Test")
        verify(productoApi).obtenerProductos()
    }

    @Test
    fun `actualizarProductoRemoto envia body y mapea respuesta`() = runTest {
        val dto = ProductoDto(
            idProducto = 1,
            nombre = "Updated",
            precio = 250.0,
            stock = 20,
            estado = "DISPONIBLE",
            categoria = CategoriaSimpleDto(idCategoria = 1, nombre = "Test")
        )
        `when`(productoApi.actualizarProducto(1L, anyMap())).thenReturn(dto)

        val result = repository.actualizarProductoRemoto(1, "Updated", 250.0, 20, 1)

        assertThat(result.nombre).isEqualTo("Updated")
        verify(productoApi).actualizarProducto(eq(1L), anyMap())
    }

    @Test
    fun `eliminarProductoRemoto llama a API con id correcto`() = runTest {
        repository.eliminarProductoRemoto(1)

        verify(productoApi).eliminarProducto(1L)
    }

    @Test
    fun `cambiarEstadoRemoto envia estado correcto`() = runTest {
        repository.cambiarEstadoRemoto(1, EstadoProducto.NO_DISPONIBLE)
        val captor = ArgumentCaptor.forClass(Map::class.java as Class<Map<String, Any>>)
        verify(productoApi).cambiarEstadoProducto(eq(1L), captor.capture())
        val body = captor.value
        assertThat(body["estado"]).isEqualTo("NO_DISPONIBLE")
    }
}