package com.recuperacion.agustin.modelo

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.recuperacion.agustin.viewmodel.AlimentosMVVM

class AlimentosMVVMFactory(
    private val alimentosRepository: AlimentosRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlimentosMVVM::class.java)) {
            return AlimentosMVVM( alimentosRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
