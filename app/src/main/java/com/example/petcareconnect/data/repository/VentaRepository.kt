package com.example.petcareconnect.data.repository

import com.example.petcareconnect.data.db.dao.VentaDao
import com.example.petcareconnect.data.model.Venta
import kotlinx.coroutines.flow.Flow

class VentaRepository(private val ventaDao: VentaDao) {

    fun getAllVentas(): Flow<List<Venta>> {
        return ventaDao.getAllVentas()
    }

    suspend fun insert(venta: Venta): Long {
        return ventaDao.insert(venta)
    }

    suspend fun deleteById(id: Int) {
        ventaDao.deleteById(id)
    }

    suspend fun getById(id: Int): Venta? {
        return ventaDao.getById(id)
    }
}