package com.example.medisync.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicamentos")
data class BddMedicamentos(

    @PrimaryKey
    val id: String,

    val nombreMedicamento: String,

    val fechaCaducidad: Long,

    val cantidad: Double,

    val unidadMedida: String,
)