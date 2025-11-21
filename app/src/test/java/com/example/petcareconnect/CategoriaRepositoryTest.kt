package com.example.petcareconnect

import com.example.petcareconnect.data.db.dao.CategoriaDao
import com.example.petcareconnect.data.model.Categoria
import com.example.petcareconnect.data.repository.CategoriaRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class CategoriaRepositoryTest {

    @Mock
    private lateinit var categoriaDao: CategoriaDao

    private lateinit var repository: CategoriaRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = CategoriaRepository(categoriaDao)
    }

    @Test
    fun `getAllCategorias retorna flow de DAO`() {
        val categorias = listOf(Categoria(idCategoria = 1, nombre = "Test"))
        `when`(categoriaDao.getAll()).thenReturn(flowOf(categorias))

        val result = repository.getAllCategorias()

        assertThat(result).isEqualTo(flowOf(categorias))
        verify(categoriaDao).getAll()
    }

    @Test
    fun `getAllOnce retorna lista de DAO`() = runTest {
        val categorias = listOf(Categoria(idCategoria = 1, nombre = "Test"))
        `when`(categoriaDao.getAllOnce()).thenReturn(categorias)

        val result = repository.getAllOnce()

        assertThat(result).isEqualTo(categorias)
        verify(categoriaDao).getAllOnce()
    }

    @Test
    fun `insert llama a DAO insert`() = runTest {
        val categoria = Categoria(idCategoria = 1, nombre = "Test")

        repository.insert(categoria)

        verify(categoriaDao).insert(categoria)
    }

    @Test
    fun `deleteById llama a DAO deleteById`() = runTest {
        val id = 1

        repository.deleteById(id)

        verify(categoriaDao).deleteById(id)
    }

    @Test
    fun `update llama a DAO update`() = runTest {
        val categoria = Categoria(idCategoria = 1, nombre = "Updated")

        repository.update(categoria)

        verify(categoriaDao).update(categoria)
    }
}
