package com.example.petcareconnect

import com.example.petcareconnect.data.db.dao.CarritoDao
import com.example.petcareconnect.data.model.Carrito
import com.example.petcareconnect.data.repository.CarritoRepository
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class CarritoRepositoryTest {

    private lateinit var carritoDao: CarritoDao
    private lateinit var repository: CarritoRepository

    @Before
    fun setup() {
        carritoDao = mockk()
        repository = CarritoRepository(carritoDao)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getAllItems retorna flow de items del DAO`() = runTest {
        // Given
        val expectedItems = listOf(
            Carrito(idItem = 1, idProducto = 10L, nombre = "Collar", precio = 15.99, cantidad = 2, stock = 10),
            Carrito(idItem = 2, idProducto = 20L, nombre = "Pelota", precio = 8.50, cantidad = 1, stock = 5)
        )
        every { carritoDao.getAllItems() } returns flowOf(expectedItems)

        // When
        val result = repository.getAllItems().first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Collar", result[0].nombre)
        assertEquals(15.99, result[0].precio, 0.01)
        assertEquals(2, result[0].cantidad)
        verify(exactly = 1) { carritoDao.getAllItems() }
    }

    @Test
    fun `getAllItems retorna lista vacia cuando no hay items`() = runTest {
        // Given
        every { carritoDao.getAllItems() } returns flowOf(emptyList())

        // When
        val result = repository.getAllItems().first()

        // Then
        assertTrue(result.isEmpty())
        verify(exactly = 1) { carritoDao.getAllItems() }
    }

    @Test
    fun `insertItem llama al DAO insert`() = runTest {
        // Given
        val newItem = Carrito(idItem = 0, idProducto = 30L, nombre = "Correa", precio = 12.99, cantidad = 1, stock = 15)
        coEvery { carritoDao.insert(newItem) } just Runs

        // When
        repository.insertItem(newItem)

        // Then
        coVerify(exactly = 1) { carritoDao.insert(newItem) }
    }

    @Test
    fun `deleteById llama al DAO deleteById con el id correcto`() = runTest {
        // Given
        val itemId = 5
        coEvery { carritoDao.deleteById(itemId) } just Runs

        // When
        repository.deleteById(itemId)

        // Then
        coVerify(exactly = 1) { carritoDao.deleteById(itemId) }
    }

    @Test
    fun `updateItem llama al DAO update`() = runTest {
        // Given
        val updatedItem = Carrito(idItem = 1, idProducto = 10L, nombre = "Collar Premium", precio = 18.99, cantidad = 3, stock = 10)
        coEvery { carritoDao.update(updatedItem) } just Runs

        // When
        repository.updateItem(updatedItem)

        // Then
        coVerify(exactly = 1) { carritoDao.update(updatedItem) }
    }

    @Test
    fun `clearAll llama al DAO clearAll para vaciar carrito`() = runTest {
        // Given
        coEvery { carritoDao.clearAll() } just Runs

        // When
        repository.clearAll()

        // Then
        coVerify(exactly = 1) { carritoDao.clearAll() }
    }
}

