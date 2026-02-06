package com.example.medisync.repository.repo_dosis

import com.example.medisync.data.entity.BddDosis
import kotlinx.coroutines.flow.Flow

interface DosisRepositoryInterface {

    suspend fun insertar(dosis: BddDosis)

    suspend fun actualizar(dosis: BddDosis)

    suspend fun eliminar(dosis: BddDosis)

    fun obtenerTodas(): Flow<List<BddDosis>>

    fun obtenerPorId(id: String): Flow<BddDosis?>
}
