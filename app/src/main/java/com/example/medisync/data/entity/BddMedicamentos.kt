package com.example.medisync.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicamentos")
data class BddMedicamentos(

    @PrimaryKey
    val id: String,

    val nombre: String,

    val caducidad: String,

    val cantidad: Double,

    val tipo: String,
)