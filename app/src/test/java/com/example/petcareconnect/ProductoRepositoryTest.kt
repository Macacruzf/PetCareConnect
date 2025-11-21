package com.example.petcareconnect

import com.example.petcareconnect.data.db.dao.ProductoDao
import com.example.petcareconnect.data.model.EstadoProducto
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.repository.ProductoRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class ProductoRepositoryTest {

    @Mock
    private lateinit var productoDao: ProductoDao

    private lateinit var repository: ProductoRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = ProductoRepository(productoDao)
    }

    @Test
    fun `getAllProductos retorna flow de DAO`() {
        val productos = listOf(Producto(idProducto = 1, nombre = "Test", precio = 100.0, stock = 10, categoriaId = 1, estado = EstadoProducto.DISPONIBLE))
        `when`(productoDao.getAll()).thenReturn(flowOf(productos))

        val result = repository.getAllProductos()

        assertThat(result).isEqualTo(flowOf(productos))
        verify(productoDao).getAll()
    }

    @Test
    fun `getAllOnce retorna lista de DAO`() = runTest {
        val productos = listOf(Producto(idProducto = 1, nombre = "Test", precio = 100.0, stock = 10, categoriaId = 1, estado = EstadoProducto.DISPONIBLE))
        `when`(productoDao.getAllOnce()).thenReturn(productos)

        val result = repository.getAllOnce()

        assertThat(result).isEqualTo(productos)
        verify(productoDao).getAllOnce()
    }

    @Test
    fun `insert llama a DAO insert`() = runTest {
        val producto = Producto(idProducto = 1, nombre = "Test", precio = 100.0, stock = 10, categoriaId = 1, estado = EstadoProducto.DISPONIBLE)

        repository.insert(producto)

        verify(productoDao).insert(producto)
    }

    @Test
    fun `deleteById llama a DAO deleteById`() = runTest {
        val id = 1

        repository.deleteById(id)

        verify(productoDao).deleteById(id)
    }

    @Test
    fun `updateStock llama a DAO updateStock`() = runTest {
        val id = 1
        val nuevoStock = 20

        repository.updateStock(id, nuevoStock)

        verify(productoDao).updateStock(id, nuevoStock)
    }

    @Test
    fun `updateEstado llama a DAO updateEstado`() = runTest {
        val id = 1
        val nuevoEstado = EstadoProducto.NO_DISPONIBLE

        repository.updateEstado(id, nuevoEstado)

        verify(productoDao).updateEstado(id, nuevoEstado)
    }

    @Test
    fun `update llama a DAO update`() = runTest {
        val producto = Producto(idProducto = 1, nombre = "Updated", precio = 150.0, stock = 15, categoriaId = 1, estado = EstadoProducto.DISPONIBLE)

        repository.update(producto)

        verify(productoDao).update(producto)
    }

    @Test
    fun `deleteAll llama a DAO deleteAll`() = runTest {
        repository.deleteAll()

        verify(productoDao).deleteAll()
    }
}
