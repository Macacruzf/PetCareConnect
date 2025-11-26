package com.example.petcareconnect

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.petcareconnect.data.remote.dto.*
import com.example.petcareconnect.data.remote.repository.TicketRemoteRepository
import com.example.petcareconnect.ui.viewmodel.TicketViewModel
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
class TicketViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: TicketViewModel
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var remoteRepository: TicketRemoteRepository

    @Before
    fun setup() {
        remoteRepository = mockk(relaxed = true)
        Dispatchers.setMain(testDispatcher)

        viewModel = TicketViewModel(remoteRepository)
        testDispatcher.scheduler.advanceUntilIdle() // Inicializar el ViewModel
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `onComentarioChange actualiza estado correctamente`() {
        // When
        viewModel.onComentarioChange("Este es un comentario de prueba")

        // Then
        assertEquals("Este es un comentario de prueba", viewModel.state.value.comentario)
    }

    @Test
    fun `onCalificacionChange actualiza estado correctamente`() {
        // When
        viewModel.onCalificacionChange(5)

        // Then
        assertEquals(5, viewModel.state.value.calificacion)
    }

    @Test
    fun `onNuevoComentarioChange actualiza estado correctamente`() {
        // When
        viewModel.onNuevoComentarioChange("Respuesta del admin")

        // Then
        assertEquals("Respuesta del admin", viewModel.state.value.nuevoComentario)
    }

    @Test
    fun `resetState limpia el estado correctamente`() {
        // Given
        viewModel.onComentarioChange("Comentario")
        viewModel.onCalificacionChange(4)

        // When
        viewModel.resetState()

        // Then
        assertEquals("", viewModel.state.value.comentario)
        assertEquals(0, viewModel.state.value.calificacion)
        assertTrue(viewModel.state.value.tickets.isEmpty())
    }

    @Test
    fun `loadTickets carga tickets exitosamente`() = runTest {
        // Given
        val idProducto = 10L
        val mockTickets = listOf(
            TicketResponse(
                idTicket = 1L,
                fechaCreacion = "2025-11-25",
                idUsuario = 1L,
                idProducto = 10L,
                clasificacion = 5,
                comentario = "Excelente",
                comentarios = emptyList()
            ),
            TicketResponse(
                idTicket = 2L,
                fechaCreacion = "2025-11-24",
                idUsuario = 2L,
                idProducto = 10L,
                clasificacion = 4,
                comentario = "Muy bueno",
                comentarios = emptyList()
            )
        )
        coEvery { remoteRepository.listarPorProducto(idProducto) } returns mockTickets

        // When
        viewModel.loadTickets(idProducto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(2, viewModel.state.value.tickets.size)
        assertEquals("Excelente", viewModel.state.value.tickets[0].comentario)
        assertNull(viewModel.state.value.errorMsg)
        coVerify(exactly = 1) { remoteRepository.listarPorProducto(idProducto) }
    }

    @Test
    fun `loadTickets maneja error correctamente`() = runTest {
        // Given
        val idProducto = 10L
        coEvery { remoteRepository.listarPorProducto(idProducto) } throws Exception("Error de red")

        // When
        viewModel.loadTickets(idProducto)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        // Verificar que el mensaje de error está presente
        assertNotNull(viewModel.state.value.errorMsg)
        coVerify(exactly = 1) { remoteRepository.listarPorProducto(idProducto) }
    }

    @Test
    fun `loadComentarios carga comentarios exitosamente`() = runTest {
        // Given
        val idTicket = 5L
        val mockComentarios = listOf(
            ComentarioResponse(
                idComentario = 1L,
                idUsuario = 1L,
                mensaje = "Gracias por tu compra",
                fecha = "2025-11-25",
                tipoMensaje = "SOPORTE"
            )
        )
        coEvery { remoteRepository.obtenerComentarios(idTicket) } returns mockComentarios

        // When
        viewModel.loadComentarios(idTicket)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(1, viewModel.state.value.comentarios.size)
        assertEquals("Gracias por tu compra", viewModel.state.value.comentarios[0].mensaje)
        assertNull(viewModel.state.value.errorMsg)
        coVerify(exactly = 1) { remoteRepository.obtenerComentarios(idTicket) }
    }

    @Test
    fun `enviarTicket con comentario vacio muestra error`() = runTest {
        // Given
        viewModel.onCalificacionChange(5)
        viewModel.onComentarioChange("")

        // When
        viewModel.enviarTicket(10L, 1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        // El mensaje de error debe estar presente inmediatamente
        assertNotNull(viewModel.state.value.errorMsg)
        assertTrue(viewModel.state.value.errorMsg!!.contains("comentario"))
        coVerify(exactly = 0) { remoteRepository.crearTicket(any()) }
    }

    @Test
    fun `enviarTicket con calificacion cero muestra error`() = runTest {
        // Given
        viewModel.onComentarioChange("Buen producto")
        viewModel.onCalificacionChange(0)

        // When
        viewModel.enviarTicket(10L, 1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        // El mensaje de error debe estar presente inmediatamente
        assertNotNull(viewModel.state.value.errorMsg)
        assertTrue(viewModel.state.value.errorMsg!!.contains("estrellas"))
        coVerify(exactly = 0) { remoteRepository.crearTicket(any()) }
    }

    @Test
    fun `enviarTicket exitoso limpia campos y muestra mensaje`() = runTest {
        // Given
        val idProducto = 10L
        val idUsuario = 1L
        viewModel.onComentarioChange("Excelente producto")
        viewModel.onCalificacionChange(5)

        val mockTicketResponse = TicketResponse(
            idTicket = 1L,
            fechaCreacion = "2025-11-25",
            idUsuario = idUsuario,
            idProducto = idProducto,
            clasificacion = 5,
            comentario = "Excelente producto",
            comentarios = emptyList()
        )
        coEvery { remoteRepository.crearTicket(any()) } returns mockTicketResponse
        coEvery { remoteRepository.listarPorProducto(idProducto) } returns emptyList()

        // When
        viewModel.enviarTicket(idProducto, idUsuario)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals("", viewModel.state.value.comentario)
        assertEquals(0, viewModel.state.value.calificacion)
        assertEquals("¡Gracias por tu reseña!", viewModel.state.value.successMsg)
        assertNull(viewModel.state.value.errorMsg)
        coVerify(exactly = 1) { remoteRepository.crearTicket(any()) }
        coVerify(exactly = 1) { remoteRepository.listarPorProducto(idProducto) }
    }

    @Test
    fun `agregarComentario con mensaje vacio muestra error`() = runTest {
        // Given
        viewModel.onNuevoComentarioChange("")

        // When
        viewModel.agregarComentario(5L, 1L)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        // Verificar que el mensaje de error está presente
        assertNotNull(viewModel.state.value.errorMsg)
        coVerify(exactly = 0) { remoteRepository.agregarComentario(any(), any()) }
    }

    @Test
    fun `agregarComentario exitoso limpia campo y muestra mensaje`() = runTest {
        // Given
        val idTicket = 5L
        val idUsuario = 1L
        viewModel.onNuevoComentarioChange("Respuesta de soporte")

        val mockComentarioResponse = ComentarioResponse(
            idComentario = 1L,
            idUsuario = idUsuario,
            mensaje = "Respuesta de soporte",
            fecha = "2025-11-25",
            tipoMensaje = "SOPORTE"
        )
        coEvery { remoteRepository.agregarComentario(idTicket, any()) } returns mockComentarioResponse
        coEvery { remoteRepository.obtenerComentarios(idTicket) } returns emptyList()

        // When
        viewModel.agregarComentario(idTicket, idUsuario)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals("", viewModel.state.value.nuevoComentario)
        assertEquals("Respuesta enviada.", viewModel.state.value.successMsg)
        assertNull(viewModel.state.value.errorMsg)
        coVerify(exactly = 1) { remoteRepository.agregarComentario(idTicket, any()) }
        coVerify(exactly = 1) { remoteRepository.obtenerComentarios(idTicket) }
    }
}

