package com.example.petcareconnect

import com.example.petcareconnect.data.db.dao.ProductoDao
import com.example.petcareconnect.data.model.EstadoProducto
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.repository.ProductoRepository
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class ProductoRepositoryTest {

    private lateinit var productoDao: ProductoDao
    private lateinit var repository: ProductoRepository

    @Before
    fun setup() {
        productoDao = mockk()
        repository = ProductoRepository(productoDao)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getAllProductos retorna flow de DAO`() = runTest {
        // Given
        val productos = listOf(Producto(idProducto = 1, nombre = "Test", precio = 100.0, stock = 10, categoriaId = 1, estado = EstadoProducto.DISPONIBLE))
        every { productoDao.getAll() } returns flowOf(productos)

        // When
        val result = repository.getAllProductos().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Test", result[0].nombre)
        verify(exactly = 1) { productoDao.getAll() }
    }

    @Test
    fun `getAllOnce retorna lista de DAO`() = runTest {
        // Given
        val productos = listOf(Producto(idProducto = 1, nombre = "Test", precio = 100.0, stock = 10, categoriaId = 1, estado = EstadoProducto.DISPONIBLE))
        coEvery { productoDao.getAllOnce() } returns productos

        // When
        val result = repository.getAllOnce()

        // Then
        assertEquals(1, result.size)
        assertEquals("Test", result[0].nombre)
        coVerify(exactly = 1) { productoDao.getAllOnce() }
    }

    @Test
    fun `insert llama a DAO insert`() = runTest {
        // Given
        val producto = Producto(idProducto = 1, nombre = "Test", precio = 100.0, stock = 10, categoriaId = 1, estado = EstadoProducto.DISPONIBLE)
        coEvery { productoDao.insert(producto) } just Runs

        // When
        repository.insert(producto)

        // Then
        coVerify(exactly = 1) { productoDao.insert(producto) }
    }

    @Test
    fun `deleteById llama a DAO deleteById`() = runTest {
        // Given
        val id = 1L
        coEvery { productoDao.deleteById(id) } just Runs

        // When
        repository.deleteById(id)

        // Then
        coVerify(exactly = 1) { productoDao.deleteById(id) }
    }

    @Test
    fun `updateStock llama a DAO updateStock`() = runTest {
        // Given
        val id = 1
        val nuevoStock = 20
        coEvery { productoDao.updateStock(id, nuevoStock) } just Runs

        // When
        repository.updateStock(id, nuevoStock)

        // Then
        coVerify(exactly = 1) { productoDao.updateStock(id, nuevoStock) }
    }

    @Test
    fun `updateEstado llama a DAO updateEstado`() = runTest {
        // Given
        val id = 1
        val nuevoEstado = EstadoProducto.NO_DISPONIBLE
        coEvery { productoDao.updateEstado(id, nuevoEstado) } just Runs

        // When
        repository.updateEstado(id, nuevoEstado)

        // Then
        coVerify(exactly = 1) { productoDao.updateEstado(id, nuevoEstado) }
    }

    @Test
    fun `update llama a DAO update`() = runTest {
        // Given
        val producto = Producto(idProducto = 1, nombre = "Updated", precio = 150.0, stock = 15, categoriaId = 1, estado = EstadoProducto.DISPONIBLE)
        coEvery { productoDao.update(producto) } just Runs

        // When
        repository.update(producto)

        // Then
        coVerify(exactly = 1) { productoDao.update(producto) }
    }

    @Test
    fun `deleteAll llama a DAO deleteAll`() = runTest {
        // Given
        coEvery { productoDao.deleteAll() } just Runs

        // When
        repository.deleteAll()

        // Then
        coVerify(exactly = 1) { productoDao.deleteAll() }
    }
}
