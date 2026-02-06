package com.example.medisync.viewmodel.vm_dosis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medisync.data.entity.BddDosis
import com.example.medisync.repository.repo_dosis.DosisRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DosisViewModel(
    private val repository: DosisRepositoryInterface
) : ViewModel() {

    val dosis: Flow<List<BddDosis>> =
        repository.obtenerTodas()


    fun insertar(dosis: BddDosis) {
        viewModelScope.launch {
            repository.insertar(dosis)
        }
    }

    fun actualizar(dosis: BddDosis) {
        viewModelScope.launch {
            repository.actualizar(dosis)
        }
    }

    fun eliminar(dosis: BddDosis) {
        viewModelScope.launch {
            repository.eliminar(dosis)
        }
    }

}