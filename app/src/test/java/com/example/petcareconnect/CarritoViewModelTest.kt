package com.example.petcareconnect.ui.viewmodel

import com.example.petcareconnect.data.model.Carrito
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CarritoViewModelTest {

    private lateinit var viewModel: CarritoViewModel

    @Before
    fun setup() {
        viewModel = CarritoViewModel()
    }

    // 🔧 Utilidad para crear items válidos para test
    private fun crearCarrito(
        idItem: Int = 1,
        idProducto: Long = 100,
        nombre: String = "Producto Test",
        precio: Double = 1000.0,
        cantidad: Int = 1,
        stock: Int = 5,
        imagenResId: Int? = null,
        imagenUri: String? = null
    ): Carrito {
        return Carrito(
            idItem = idItem,
            idProducto = idProducto,
            nombre = nombre,
            precio = precio,
            cantidad = cantidad,
            stock = stock,
            imagenResId = imagenResId,
            imagenUri = imagenUri
        )
    }

    // -----------------------------------------------------------
    //  AGREGAR ITEM
    // -----------------------------------------------------------
    @Test
    fun `agregarItem agrega un producto nuevo`() {
        val item = crearCarrito()

        viewModel.agregarItem(item)

        val state = viewModel.state.value

        assertEquals(1, state.items.size)
        assertEquals(1, state.items.first().cantidad)
        assertEquals(1000.0, state.total, 0.0)
    }

    @Test
    fun `agregarItem no supera stock`() {
        val item = crearCarrito(stock = 1)

        viewModel.agregarItem(item)
        viewModel.agregarItem(item) // intenta agregar más

        val result = viewModel.state.value.items.first()

        assertEquals(1, result.cantidad)
        assertEquals(1000.0, viewModel.state.value.total, 0.0)
    }

    // -----------------------------------------------------------
    //  INCREMENTAR
    // -----------------------------------------------------------
    @Test
    fun `incrementarCantidad aumenta sin superar stock`() {
        val item = crearCarrito(cantidad = 1, stock = 2)

        viewModel.agregarItem(item)

        viewModel.incrementarCantidad(1)
        viewModel.incrementarCantidad(1) // stock máximo

        val result = viewModel.state.value.items.first()

        assertEquals(2, result.cantidad)
        assertEquals(2000.0, viewModel.state.value.total, 0.0)
    }

    // -----------------------------------------------------------
    //  DECREMENTAR
    // -----------------------------------------------------------
    @Test
    fun `decrementarCantidad no baja de 1`() {
        val item = crearCarrito(cantidad = 1)

        viewModel.agregarItem(item)
        viewModel.decrementarCantidad(1)

        val result = viewModel.state.value.items.first()

        assertEquals(1, result.cantidad)
    }

    // -----------------------------------------------------------
    //  ACTUALIZAR CANTIDAD DIRECTA
    // -----------------------------------------------------------
    @Test
    fun `actualizarCantidad respeta stock máximo`() {
        val item = crearCarrito(stock = 3)

        viewModel.agregarItem(item)
        viewModel.actualizarCantidad(item, 10) // intenta 10 → queda 3

        val result = viewModel.state.value.items.first()

        assertEquals(3, result.cantidad)
        assertEquals(3000.0, viewModel.state.value.total, 0.0)
    }

    // -----------------------------------------------------------
    //  ELIMINAR ITEM
    // -----------------------------------------------------------
    @Test
    fun `eliminarItem remueve el item`() {
        val item = crearCarrito()

        viewModel.agregarItem(item)
        viewModel.eliminarItem(1)

        val state = viewModel.state.value

        assertTrue(state.items.isEmpty())
        assertEquals(0.0, state.total, 0.0)
    }

    // -----------------------------------------------------------
    //  VACIAR CARRITO
    // -----------------------------------------------------------
    @Test
    fun `vaciarCarrito reinicia todo`() {
        val item = crearCarrito()
        viewModel.agregarItem(item)

        viewModel.vaciarCarrito()

        val state = viewModel.state.value
        assertTrue(state.items.isEmpty())
        assertEquals(0.0, state.total, 0.0)
    }

    // -----------------------------------------------------------
    //  DESCONTAR BACKEND
    // -----------------------------------------------------------
    @Test
    fun `descontarStockBackend llama a funcion por cada item`() = runTest {

        val mockFn: suspend (Long, Int) -> Unit = mockk(relaxed = true)

        val item = crearCarrito(cantidad = 3)
        viewModel.agregarItem(item)

        viewModel.descontarStockBackend(mockFn)

        coVerify(exactly = 1) { mockFn(100, 3) }
    }
}
