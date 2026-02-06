package com.example.medisync.viewmodel.vm_dosis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medisync.repository.repo_dosis.DosisRepositoryInterface

class DosisViewModelFactory(
    private val repository: DosisRepositoryInterface
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DosisViewModel::class.java)) {
            return DosisViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida: ${modelClass.name}")
    }
}