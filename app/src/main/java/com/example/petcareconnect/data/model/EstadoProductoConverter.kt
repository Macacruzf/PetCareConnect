package com.example.petcareconnect.data.db.converters

import androidx.room.TypeConverter
import com.example.petcareconnect.data.model.EstadoProducto

class EstadoProductoConverter {

    @TypeConverter
    fun fromEstado(value: EstadoProducto): String = value.name

    @TypeConverter
    fun toEstado(value: String): EstadoProducto = EstadoProducto.valueOf(value)
}
