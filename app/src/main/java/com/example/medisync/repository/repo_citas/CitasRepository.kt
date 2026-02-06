package com.example.medisync.repository.repo_citas

import com.example.medisync.data.dao.DaoCitas
import com.example.medisync.data.entity.BddCitas
import com.example.medisync.repository.repo_citas.CitasRepositoryInterface
import kotlinx.coroutines.flow.Flow

/**
 * Implementación concreta del repositorio de Citas.
 * Actúa como mediador entre el DAO (Room) y el ViewModel.
 *
 * @property daoCitas El objeto de acceso a datos para realizar operaciones en la tabla de citas.
 */
class CitasRepository(
    private val daoCitas: DaoCitas
) : CitasRepositoryInterface {

    /** Inserta una cita llamando al DAO */
    override suspend fun insertar(cita: BddCitas) {
        daoCitas.insert(cita)
    }

    /** Actualiza la información de una cita en la BBDD */
    override suspend fun actualizar(cita: BddCitas) {
        daoCitas.update(cita)
    }

    /** Elimina una cita de la BBDD a través del DAO */
    override suspend fun eliminar(cita: BddCitas) {
        daoCitas.delete(cita)
    }

    /** Retorna un Flow con la lista de todas las citas, permitiendo observar cambios en tiempo real */
    override fun obtenerTodas(): Flow<List<BddCitas>> {
        return daoCitas.getAll()
    }

    /** Obtiene una cita por su ID de forma reactiva */
    override fun obtenerPorId(id: String): Flow<BddCitas?> {
        return daoCitas.getById(id)
    }
}