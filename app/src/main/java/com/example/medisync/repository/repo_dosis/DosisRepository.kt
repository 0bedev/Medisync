package com.example.medisync.repository.repo_dosis

import com.example.medisync.data.dao.DaoDosis
import com.example.medisync.data.entity.BddDosis
import com.example.medisync.repository.repo_dosis.DosisRepositoryInterface
import kotlinx.coroutines.flow.Flow

class DosisRepository(
    private val daoDosis: DaoDosis
) : DosisRepositoryInterface {

    override suspend fun insertar(dosis: BddDosis) {
        daoDosis.insert(dosis)
    }

    override suspend fun actualizar(dosis: BddDosis) {
        daoDosis.update(dosis)
    }

    override suspend fun eliminar(dosis: BddDosis) {
        daoDosis.delete(dosis)
    }

    override fun obtenerTodas(): Flow<List<BddDosis>> {
        return daoDosis.getAll()
    }

    override fun obtenerPorId(id: String): Flow<BddDosis?> {
        return daoDosis.getById(id)
    }
}