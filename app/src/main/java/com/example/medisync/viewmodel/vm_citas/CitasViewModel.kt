package com.example.medisync.viewmodel.vm_citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medisync.data.entity.BddCitas
import com.example.medisync.repository.repo_citas.CitasRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona la lógica de presentación y el estado para la UI de Citas.
 * No accede a la base de datos directamente, sino que lo hace a través de un repositorio.
 *
 * @property repository La interfaz para acceder a las operaciones de datos de citas.
 */
class CitasViewModel(
    private val repository: CitasRepositoryInterface
) : ViewModel() {

    /**
     * Flujo reactivo de todas las citas. La UI puede observar este Flow para actualizarse
     * automáticamente cuando hay cambios en la base de datos.
     */
    val citas: Flow<List<BddCitas>> =
        repository.obtenerTodas()


    /**
     * Inserta una nueva cita lanzando una corrutina dentro del ciclo de vida del ViewModel.
     * @param cita El objeto de datos de la cita a insertar.
     */
    fun insertar(cita: BddCitas) {
        viewModelScope.launch {
            repository.insertar(cita)
        }
    }


    /**
     * Actualiza una cita existente lanzando una corrutina dentro del ciclo de vida del ViewModel.
     * @param cita El objeto de datos de la cita a actualizar.
     */
    fun actualizar(cita: BddCitas) {
        viewModelScope.launch {
            repository.actualizar(cita)
        }
    }


    /**
     * Elimina una cita existente de forma asíncrona dentro del viewModelScope.
     * @param cita El objeto de datos de la cita que se desea borrar.
     */
    fun eliminar(cita: BddCitas) {
        viewModelScope.launch {
            repository.eliminar(cita)
        }
    }

}