package com.example.medisync.repository.repo_medicamentos

import com.example.medisync.data.dao.DaoMedicamentos
import com.example.medisync.data.entity.BddMedicamentos
import com.example.medisync.repository.repo_medicamentos.MedicamentosRepositoryInterface
import kotlinx.coroutines.flow.Flow

class MedicamentosRepository(
    private val daoMedicamentos: DaoMedicamentos
) : MedicamentosRepositoryInterface{

    override suspend fun insertar(medicamento: BddMedicamentos) {
        daoMedicamentos.insert(medicamento)
    }

    override suspend fun actualizar(medicamento: BddMedicamentos) {
        daoMedicamentos.update(medicamento)
    }

    override suspend fun eliminar(medicamento: BddMedicamentos) {
        daoMedicamentos.delete(medicamento)
    }

    override fun obtenerTodas(): Flow<List<BddMedicamentos>> {
        return daoMedicamentos.getAll()
    }

    override fun obtenerPorId(id: String): Flow<BddMedicamentos?> {
        return daoMedicamentos.getById(id)
    }
}