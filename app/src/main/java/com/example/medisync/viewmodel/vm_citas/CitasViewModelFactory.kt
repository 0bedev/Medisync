package com.example.medisync.viewmodel.vm_citas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medisync.repository.repo_citas.CitasRepositoryInterface

/**
 * Fábrica personalizada para crear instancias de [CitasViewModel].
 * Es necesaria porque el ViewModel de Citas requiere un repositorio en su constructor,
 * y los ViewModels por defecto no aceptan parámetros adicionales sin una Factory.
 *
 * @property repository Implementación del repositorio de citas que se inyectará en el ViewModel.
 */
class CitasViewModelFactory(
    private val repository: CitasRepositoryInterface
) : ViewModelProvider.Factory {

    /**
     * Crea una nueva instancia del ViewModel solicitado.
     *
     * @param modelClass La clase del ViewModel que se desea instanciar.
     * @return Una instancia de [CitasViewModel] con el repositorio inyectado.
     * @throws IllegalArgumentException Si la clase solicitada no es [CitasViewModel].
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CitasViewModel::class.java)) {
            return CitasViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida: ${modelClass.name}")
    }
}