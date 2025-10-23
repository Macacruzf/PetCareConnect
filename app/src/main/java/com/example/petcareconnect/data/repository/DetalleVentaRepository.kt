package com.example.petcareconnect.data.repository

import com.example.petcareconnect.data.db.dao.DetalleVentaDao
import com.example.petcareconnect.data.model.DetalleVenta
import kotlinx.coroutines.flow.Flow

class DetalleVentaRepository(private val dao: DetalleVentaDao) {

    suspend fun insert(detalle: DetalleVenta) = dao.insert(detalle)

    fun getAllDetalles(): Flow<List<DetalleVenta>> = dao.getAllDetalles()

    suspend fun getByVentaId(ventaId: Int): List<DetalleVenta> = dao.getByVentaId(ventaId)
}
