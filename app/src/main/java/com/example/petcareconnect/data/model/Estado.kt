package com.example.petcareconnect.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "estados")
data class Estado(
    @PrimaryKey(autoGenerate = true) val idEstado: Int = 0,
    val nombre: String
)
