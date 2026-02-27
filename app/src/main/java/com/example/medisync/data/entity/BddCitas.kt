package com.example.medisync.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "citas")
data class BddCitas(

    @PrimaryKey
    val id: String,

    val citaNombre: String,

    val doctor: String,

    val especialidad: String,

    val fechaHora: Long,

    val nota: String
)