package com.example.medisync.repository.repo_citas

import com.example.medisync.data.entity.BddCitas
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define el contrato para el repositorio de Citas.
 * Sigue el patrón Repository para abstraer el origen de los datos (BBDD, API, etc.)
 */
interface CitasRepositoryInterface {

    /** Inserta una nueva cita de forma asíncrona */
    suspend fun insertar(cita: BddCitas)

    /** Actualiza una cita existente de forma asíncrona */
    suspend fun actualizar(cita: BddCitas)

    /** Elimina una cita de la base de datos de forma asíncrona */
    suspend fun eliminar(cita: BddCitas)

    /** Obtiene todas las citas registradas mediante un flujo (Flow) reactivo */
    fun obtenerTodas(): Flow<List<BddCitas>>

    /** Busca una cita específica por su identificador único */
    fun obtenerPorId(id: String): Flow<BddCitas?>

}