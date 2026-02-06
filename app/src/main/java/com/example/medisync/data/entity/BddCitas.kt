package com.example.medisync.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "citas")
data class BddCitas(

    @PrimaryKey
    val id: String,

    val doctor: String,

    val especialidad: String,

    val fecha: String,

    val hora: String,

    val nota: String
)