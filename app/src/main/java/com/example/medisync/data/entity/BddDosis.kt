package com.example.medisync.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dosis")
data class BddDosis(

    @PrimaryKey
    val id: String,

    val dosis_consumo: String,

    val fecha: String,

    val hora: String,
)