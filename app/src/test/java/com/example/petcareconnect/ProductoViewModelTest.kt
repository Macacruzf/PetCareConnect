package com.example.petcareconnect

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.petcareconnect.data.model.EstadoProducto
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.remote.ProductoRemoteRepository
import com.example.petcareconnect.ui.viewmodel.ProductoViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class ProductoViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ProductoViewModel
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var remoteRepository: ProductoRemoteRepository

    @Before
    fun setup() {
        remoteRepository = mockk(relaxed = true)
        Dispatchers.setMain(testDispatcher)

        // Mock para que no falle la inicialización
        coEvery { remoteRepository.getAllProductosRemotos() } returns emptyList()

        viewModel = ProductoViewModel(remoteRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `init carga productos desde API`() = runTest {
        // Given
        val productos = listOf(
            Producto(idProducto = 1, nombre = "Test", precio = 100.0, stock = 10, categoriaId = 1, estado = EstadoProducto.DISPONIBLE)
        )
        coEvery { remoteRepository.getAllProductosRemotos() } returns productos

        // When
        val newViewModel = ProductoViewModel(remoteRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(atLeast = 1) { remoteRepository.getAllProductosRemotos() }
    }

    @Test
    fun `onNombreChange actualiza estado correctamente`() {
        // When
        viewModel.onNombreChange("Producto Test")

        // Then
        assertEquals("Producto Test", viewModel.state.value.nombre)
    }

    @Test
    fun `onPrecioChange actualiza estado correctamente`() {
        // When
        viewModel.onPrecioChange("150.5")

        // Then
        assertEquals("150.5", viewModel.state.value.precio)
    }

    @Test
    fun `onStockChange actualiza estado correctamente`() {
        // When
        viewModel.onStockChange("20")

        // Then
        assertEquals("20", viewModel.state.value.stock)
    }

    @Test
    fun `cargarProductoParaEdicion actualiza estado`() {
        // Given
        val producto = Producto(
            idProducto = 1,
            nombre = "Edit Test",
            precio = 200.0,
            stock = 15,
            categoriaId = 2,
            estado = EstadoProducto.NO_DISPONIBLE
        )

        // When
        viewModel.cargarProductoParaEdicion(producto)

        // Then
        assertEquals(1L, viewModel.state.value.productoEnEdicionId)
        assertEquals("Edit Test", viewModel.state.value.nombre)
        assertEquals("200.0", viewModel.state.value.precio)
        assertEquals("15", viewModel.state.value.stock)
    }
}
