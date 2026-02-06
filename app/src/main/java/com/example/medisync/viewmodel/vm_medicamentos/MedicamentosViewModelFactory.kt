package com.example.medisync.viewmodel.vm_medicamentos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medisync.repository.repo_medicamentos.MedicamentosRepositoryInterface

class MedicamentosViewModelFactory(
    private val repository: MedicamentosRepositoryInterface
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicamentosViewModel::class.java)) {
            return MedicamentosViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida: ${modelClass.name}")
    }
}