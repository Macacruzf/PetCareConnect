package com.example.petcareconnect

import com.example.petcareconnect.data.db.dao.TicketDao
import com.example.petcareconnect.data.model.Ticket
import com.example.petcareconnect.data.repository.TicketRepository
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class TicketRepositoryTest {

    private lateinit var ticketDao: TicketDao
    private lateinit var repository: TicketRepository

    @Before
    fun setup() {
        ticketDao = mockk()
        repository = TicketRepository(ticketDao)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getTicketsByProducto retorna flow de tickets del DAO`() = runTest {
        // Given
        val idProducto = 10
        val expectedTickets = listOf(
            Ticket(
                idTicket = 1,
                idProducto = 10,
                nombreUsuario = "Juan Pérez",
                comentario = "Excelente producto",
                calificacion = 5,
                fecha = System.currentTimeMillis()
            ),
            Ticket(
                idTicket = 2,
                idProducto = 10,
                nombreUsuario = "María García",
                comentario = "Muy bueno",
                calificacion = 4,
                fecha = System.currentTimeMillis()
            )
        )
        every { ticketDao.getTicketsByProducto(idProducto) } returns flowOf(expectedTickets)

        // When
        val result = repository.getTicketsByProducto(idProducto).first()

        // Then
        assertEquals(2, result.size)
        assertEquals("Juan Pérez", result[0].nombreUsuario)
        assertEquals(5, result[0].calificacion)
        assertEquals("María García", result[1].nombreUsuario)
        assertEquals(4, result[1].calificacion)
        verify(exactly = 1) { ticketDao.getTicketsByProducto(idProducto) }
    }

    @Test
    fun `getTicketsByProducto retorna lista vacia cuando no hay tickets`() = runTest {
        // Given
        val idProducto = 99
        every { ticketDao.getTicketsByProducto(idProducto) } returns flowOf(emptyList())

        // When
        val result = repository.getTicketsByProducto(idProducto).first()

        // Then
        assertTrue(result.isEmpty())
        verify(exactly = 1) { ticketDao.getTicketsByProducto(idProducto) }
    }

    @Test
    fun `insert llama al DAO insert con el ticket correcto`() = runTest {
        // Given
        val newTicket = Ticket(
            idTicket = 0,
            idProducto = 15,
            nombreUsuario = "Pedro López",
            comentario = "Buen producto, llegó rápido",
            calificacion = 4,
            fecha = System.currentTimeMillis()
        )
        coEvery { ticketDao.insert(newTicket) } just Runs

        // When
        repository.insert(newTicket)

        // Then
        coVerify(exactly = 1) { ticketDao.insert(newTicket) }
    }

    @Test
    fun `delete llama al DAO deleteTicket con el id correcto`() = runTest {
        // Given
        val ticketId = 7
        coEvery { ticketDao.deleteTicket(ticketId) } just Runs

        // When
        repository.delete(ticketId)

        // Then
        coVerify(exactly = 1) { ticketDao.deleteTicket(ticketId) }
    }

    @Test
    fun `getTicketsByProducto retorna tickets ordenados por fecha`() = runTest {
        // Given
        val idProducto = 5
        val fecha1 = System.currentTimeMillis() - 1000
        val fecha2 = System.currentTimeMillis()

        val expectedTickets = listOf(
            Ticket(
                idTicket = 1,
                idProducto = 5,
                nombreUsuario = "Usuario 1",
                comentario = "Primer comentario",
                calificacion = 3,
                fecha = fecha1
            ),
            Ticket(
                idTicket = 2,
                idProducto = 5,
                nombreUsuario = "Usuario 2",
                comentario = "Segundo comentario",
                calificacion = 5,
                fecha = fecha2
            )
        )
        every { ticketDao.getTicketsByProducto(idProducto) } returns flowOf(expectedTickets)

        // When
        val result = repository.getTicketsByProducto(idProducto).first()

        // Then
        assertEquals(2, result.size)
        assertTrue(result[0].fecha <= result[1].fecha)
        verify(exactly = 1) { ticketDao.getTicketsByProducto(idProducto) }
    }
}

