package com.example.petcareconnect

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.petcareconnect.data.model.Categoria
import com.example.petcareconnect.data.model.EstadoProducto
import com.example.petcareconnect.data.model.Producto
import com.example.petcareconnect.data.remote.ProductoRemoteRepository
import com.example.petcareconnect.data.repository.CategoriaRepository
import com.example.petcareconnect.data.repository.ProductoRepository
import com.example.petcareconnect.ui.viewmodel.ProductoViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class ProductoViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ProductoViewModel
    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var productoRepository: ProductoRepository

    @Mock
    private lateinit var categoriaRepository: CategoriaRepository

    @Mock
    private lateinit var remoteRepository: ProductoRemoteRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        runBlocking {
            `when`(categoriaRepository.getAllOnce()).thenReturn(emptyList())
        }
        `when`(productoRepository.getAllProductos()).thenReturn(flowOf(emptyList()))
        `when`(categoriaRepository.getAllCategorias()).thenReturn(flowOf(emptyList()))
        viewModel = ProductoViewModel(productoRepository, categoriaRepository, remoteRepository)
    }

    @Test
    fun `init carga productos y categorias`() = runTest {
        val productos = listOf(Producto(idProducto = 1, nombre = "Test", precio = 100.0, stock = 10, categoriaId = 1, estado = EstadoProducto.DISPONIBLE))
        val categorias = listOf(Categoria(idCategoria = 1, nombre = "Test"))

        `when`(productoRepository.getAllProductos()).thenReturn(flowOf(productos))
        `when`(categoriaRepository.getAllCategorias()).thenReturn(flowOf(categorias))

        viewModel = ProductoViewModel(productoRepository, categoriaRepository, null)  // Sin remote

        assertThat(viewModel.state.value.productos).isEqualTo(productos)
        assertThat(viewModel.state.value.categorias).isEqualTo(categorias)
    }

    @Test
    fun `syncProductosDesdeApi actualiza estado y llama repository`() = runTest {
        val productosApi = listOf(Producto(idProducto = 1, nombre = "API Test", precio = 200.0, stock = 5, categoriaId = 1, estado = EstadoProducto.DISPONIBLE))
        `when`(remoteRepository.getAllProductosRemotos()).thenReturn(productosApi)

        viewModel.syncProductosDesdeApi()

        assertThat(viewModel.state.value.isLoading).isFalse()
        assertThat(viewModel.state.value.successMsg).isEqualTo("Productos sincronizados desde el servidor")
        verify(productoRepository).deleteAll()
        verify(productoRepository).insert(productosApi[0])
    }

    @Test
    fun `syncProductosDesdeApi maneja error`() = runTest {
        `when`(remoteRepository.getAllProductosRemotos()).thenThrow(RuntimeException("API Error"))

        viewModel.syncProductosDesdeApi()

        assertThat(viewModel.state.value.isLoading).isFalse()
        assertThat(viewModel.state.value.errorMsg).contains("Error al sincronizar")
    }

    @Test
    fun `insertProducto valida campos y inserta local si no hay API`() = runTest {
        viewModel.onNombreChange("Producto Test")
        viewModel.onPrecioChange("150.0")
        viewModel.onStockChange("20")
        viewModel.onCategoriaChange(1)

        viewModel = ProductoViewModel(productoRepository, categoriaRepository, null)  // Sin remote

        viewModel.insertProducto()

        verify(productoRepository).insert(any(Producto::class.java))
        assertThat(viewModel.state.value.successMsg).isEqualTo("Producto agregado correctamente.")
        assertThat(viewModel.state.value.nombre).isEmpty()  // Formulario limpio
    }

    @Test
    fun `insertProducto falla con nombre vacio`() = runTest {
        viewModel.onNombreChange("")
        viewModel.onPrecioChange("100")
        viewModel.onStockChange("10")
        viewModel.onCategoriaChange(1)

        viewModel.insertProducto()

        assertThat(viewModel.state.value.errorMsg).isEqualTo("El nombre es obligatorio.")
        verify(productoRepository, never()).insert(any())
    }

    @Test
    fun `insertProducto usa API si disponible`() = runTest {
        val productoApi = Producto(idProducto = 1, nombre = "API Producto", precio = 100.0, stock = 10, categoriaId = 1, estado = EstadoProducto.DISPONIBLE)
        `when`(remoteRepository.crearProductoRemoto("API Producto", 100.0, 10, 1)).thenReturn(productoApi)

        viewModel.onNombreChange("API Producto")
        viewModel.onPrecioChange("100")
        viewModel.onStockChange("10")
        viewModel.onCategoriaChange(1)

        viewModel.insertProducto()

        verify(remoteRepository).crearProductoRemoto("API Producto", 100.0, 10, 1)
        verify(productoRepository).insert(productoApi)
    }

    @Test
    fun `cargarProductoParaEdicion actualiza estado`() {
        val producto = Producto(idProducto = 1, nombre = "Edit Test", precio = 200.0, stock = 15, categoriaId = 2, estado = EstadoProducto.NO_DISPONIBLE)

        viewModel.cargarProductoParaEdicion(producto)

        assertThat(viewModel.state.value.productoEnEdicionId).isEqualTo(1)
        assertThat(viewModel.state.value.nombre).isEqualTo("Edit Test")
        assertThat(viewModel.state.value.precio).isEqualTo("200.0")
    }

    @Test
    fun `editarProducto actualiza local si no hay API`() = runTest {
        val productoExistente = Producto(idProducto = 1, nombre = "Old", precio = 100.0, stock = 10, categoriaId = 1, estado = EstadoProducto.DISPONIBLE)
        `when`(productoRepository.getAllProductos()).thenReturn(flowOf(listOf(productoExistente)))

        viewModel = ProductoViewModel(productoRepository, categoriaRepository, null)
        viewModel.cargarProductoParaEdicion(productoExistente)
        viewModel.onNombreChange("Updated")
        viewModel.onPrecioChange("150.0")

        viewModel.editarProducto()

        verify(productoRepository).update(any(Producto::class.java))
        assertThat(viewModel.state.value.successMsg).isEqualTo("Producto actualizado correctamente.")
    }

    @Test
    fun `deleteProducto elimina de API y local`() = runTest {
        viewModel.deleteProducto(1)

        verify(remoteRepository).eliminarProductoRemoto(1)
        verify(productoRepository).deleteById(1)
        assertThat(viewModel.state.value.successMsg).isEqualTo("Producto eliminado.")
    }

    @Test
    fun `cambiarEstadoManual actualiza estado via API y local`() = runTest {
        val producto = Producto(idProducto = 1, nombre = "Test", precio = 100.0, stock = 10, categoriaId = 1, estado = EstadoProducto.DISPONIBLE)
        `when`(productoRepository.getAllProductos()).thenReturn(flowOf(listOf(producto)))

        viewModel = ProductoViewModel(productoRepository, categoriaRepository, remoteRepository)

        viewModel.cambiarEstadoManual(1, EstadoProducto.NO_DISPONIBLE)

        verify(remoteRepository).cambiarEstadoRemoto(1, EstadoProducto.NO_DISPONIBLE)
        verify(productoRepository).update(any(Producto::class.java))
        assertThat(viewModel.state.value.successMsg).isEqualTo("Estado actualizado correctamente.")
    }

    @Test
    fun `onNombreChange actualiza estado`() {
        viewModel.onNombreChange("Nuevo Nombre")
        assertThat(viewModel.state.value.nombre).isEqualTo("Nuevo Nombre")
    }

    @Test
    fun `limpiarMensajes resetea mensajes`() {
        // Simula error llamando a insertProducto con nombre vacío (causa setError interno)
        viewModel.onNombreChange("")  // Nombre vacío
        viewModel.onPrecioChange("100")
        viewModel.onStockChange("10")
        viewModel.onCategoriaChange(1)
        viewModel.insertProducto()  // Llama setError("El nombre es obligatorio.")
        // Verifica que hay error
        assertThat(viewModel.state.value.errorMsg).isEqualTo("El nombre es obligatorio.")
        // Llama a limpiarMensajes
        viewModel.limpiarMensajes()
        // Verifica que se resetean
        assertThat(viewModel.state.value.errorMsg).isNull()
        assertThat(viewModel.state.value.successMsg).isNull()
    }
}
