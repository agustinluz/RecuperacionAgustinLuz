package com.recuperacion.agustin.modelo

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AlimentosMVVMFactory(
    private val application: Application,
    private val repository: AlimentosRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlimentosMVVM::class.java)) {
            return AlimentosMVVM(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
