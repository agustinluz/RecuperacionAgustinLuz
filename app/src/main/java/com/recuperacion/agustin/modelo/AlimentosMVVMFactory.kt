package com.recuperacion.agustin.modelo

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AlimentosMVVMFactory(
    private val application: Application,
    private val alimentosRepository: AlimentosRepository,
    private val ingredienteRepository: IngredienteRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlimentosMVVM::class.java)) {
            return AlimentosMVVM(application, alimentosRepository, ingredienteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
