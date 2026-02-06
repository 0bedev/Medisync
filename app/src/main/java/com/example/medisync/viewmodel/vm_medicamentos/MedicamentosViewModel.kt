package com.example.medisync.viewmodel.vm_medicamentos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medisync.data.entity.BddMedicamentos
import com.example.medisync.repository.repo_medicamentos.MedicamentosRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MedicamentosViewModel(
    private val repository: MedicamentosRepositoryInterface
) : ViewModel() {

    val medicamentos: Flow<List<BddMedicamentos>> =
        repository.obtenerTodas()


    fun insertar(medicamento: BddMedicamentos) {
        viewModelScope.launch {
            repository.insertar(medicamento)
        }
    }

    fun actualizar(medicamento: BddMedicamentos) {
        viewModelScope.launch {
            repository.actualizar(medicamento)
        }
    }

    fun eliminar(medicamento: BddMedicamentos) {
        viewModelScope.launch {
            repository.eliminar(medicamento)
        }
    }

}