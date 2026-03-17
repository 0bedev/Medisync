package com.example.medisync.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dosis")
data class BddDosis(
    @PrimaryKey
    val id: String,
    val tratamiento: String,
    val medicamento: String,
    val cantidad: Double,
    val unidadMedida: String, // Nueva columna para guardar ml o gr
    val intervaloHoras: Int,
    val fechaHoraInicio: Long,
    val duracionDias: Int,
    val activo: Boolean
)
