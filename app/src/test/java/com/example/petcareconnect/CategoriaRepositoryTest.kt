package com.example.petcareconnect

import com.example.petcareconnect.data.db.dao.CategoriaDao
import com.example.petcareconnect.data.model.Categoria
import com.example.petcareconnect.data.repository.CategoriaRepository
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class CategoriaRepositoryTest {

    private lateinit var categoriaDao: CategoriaDao
    private lateinit var repository: CategoriaRepository

    @Before
    fun setup() {
        categoriaDao = mockk()
        repository = CategoriaRepository(categoriaDao)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getAllCategorias retorna flow de DAO`() = runTest {
        // Given
        val categorias = listOf(Categoria(idCategoria = 1, nombre = "Test"))
        every { categoriaDao.getAll() } returns flowOf(categorias)

        // When
        val result = repository.getAllCategorias().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Test", result[0].nombre)
        verify(exactly = 1) { categoriaDao.getAll() }
    }

    @Test
    fun `getAllOnce retorna lista de DAO`() = runTest {
        // Given
        val categorias = listOf(Categoria(idCategoria = 1, nombre = "Test"))
        coEvery { categoriaDao.getAllOnce() } returns categorias

        // When
        val result = repository.getAllOnce()

        // Then
        assertEquals(1, result.size)
        assertEquals("Test", result[0].nombre)
        coVerify(exactly = 1) { categoriaDao.getAllOnce() }
    }

    @Test
    fun `insert llama a DAO insert`() = runTest {
        // Given
        val categoria = Categoria(idCategoria = 1, nombre = "Test")
        coEvery { categoriaDao.insert(categoria) } just Runs

        // When
        repository.insert(categoria)

        // Then
        coVerify(exactly = 1) { categoriaDao.insert(categoria) }
    }

    @Test
    fun `deleteById llama a DAO deleteById`() = runTest {
        // Given
        val id = 1L
        coEvery { categoriaDao.deleteById(id) } just Runs

        // When
        repository.deleteById(id)

        // Then
        coVerify(exactly = 1) { categoriaDao.deleteById(id) }
    }

    @Test
    fun `update llama a DAO update`() = runTest {
        // Given
        val categoria = Categoria(idCategoria = 1, nombre = "Updated")
        coEvery { categoriaDao.update(categoria) } just Runs

        // When
        repository.update(categoria)

        // Then
        coVerify(exactly = 1) { categoriaDao.update(categoria) }
    }
}
