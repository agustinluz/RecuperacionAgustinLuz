package com.recuperacion.agustin.modelo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.recuperacion.agustin.room.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlimentosMVVM(application: Application, private val repository: AlimentosRepository) : AndroidViewModel(application) {

    private val _alimentos = MutableStateFlow<List<ComponenteDieta>>(emptyList())
    val alimentos: StateFlow<List<ComponenteDieta>> = _alimentos.asStateFlow()

    init {
        viewModelScope.launch {
            repository.alimentos.collect { lista ->
                _alimentos.value = lista
            }
        }
    }

    fun agregarAlimento(alimento: ComponenteDieta) {
        viewModelScope.launch { repository.agregarAlimento(alimento) }
    }

    fun actualizarAlimento(alimento: ComponenteDieta) {
        viewModelScope.launch { repository.actualizarAlimento(alimento) }
    }

    fun eliminarAlimento(alimento: ComponenteDieta) {
        viewModelScope.launch { repository.eliminarAlimento(alimento) }
    }
}
