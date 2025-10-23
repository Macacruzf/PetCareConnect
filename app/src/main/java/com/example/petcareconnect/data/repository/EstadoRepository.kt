package com.example.petcareconnect.data.repository



import com.example.petcareconnect.data.db.dao.EstadoDao
import com.example.petcareconnect.data.model.Estado
import kotlinx.coroutines.flow.Flow

class EstadoRepository(private val estadoDao: EstadoDao) {

    fun getAllEstados(): Flow<List<Estado>> = estadoDao.getAllEstados()
}