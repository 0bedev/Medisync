package com.example.medisync.repository.repo_medicamentos

import com.example.medisync.data.entity.BddMedicamentos
import kotlinx.coroutines.flow.Flow

interface MedicamentosRepositoryInterface {

    suspend fun insertar(medicamento: BddMedicamentos)

    suspend fun actualizar(medicamento: BddMedicamentos)

    suspend fun eliminar(medicamento: BddMedicamentos)

    fun obtenerTodas(): Flow<List<BddMedicamentos>>

    fun obtenerPorId(id: String): Flow<BddMedicamentos?>
}
