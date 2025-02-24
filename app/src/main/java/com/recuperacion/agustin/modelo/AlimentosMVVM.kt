package com.recuperacion.agustin.modelo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow


class AlimentosMVVM(
    application: Application,
    private val alimentosRepository: AlimentosRepository,
    ingredienteRepository: IngredienteRepository
) : AndroidViewModel(application) {

    private val _alimentos = MutableStateFlow<List<ComponenteDieta>>(emptyList())
    val alimentos: StateFlow<List<ComponenteDieta>> = _alimentos.asStateFlow()

    private val _ingredientes = MutableStateFlow<List<Ingrediente>>(emptyList())
    val ingredientes: StateFlow<List<Ingrediente>> = _ingredientes.asStateFlow()

    private val _componenteSeleccionado = MutableStateFlow<ComponenteConIngredientes?>(null)
    val componenteSeleccionado: StateFlow<ComponenteConIngredientes?> = _componenteSeleccionado.asStateFlow()

    init {
        viewModelScope.launch {
            alimentosRepository.alimentos.collect { lista ->
                _alimentos.value = lista
            }
        }
    }

    fun seleccionarComponente(componenteId: Int) {
        viewModelScope.launch {
            alimentosRepository.obtenerComponenteConIngredientes(componenteId).collect { componente ->
                _componenteSeleccionado.value = componente
            }
        }
    }

    fun obtenerComponenteConIngredientes(componenteId: Int): Flow<ComponenteConIngredientes> {
        return alimentosRepository.obtenerComponenteConIngredientes(componenteId)
    }

    fun agregarAlimento(alimento: ComponenteDieta) {
        viewModelScope.launch { 
            alimentosRepository.agregarAlimento(alimento)
        }
    }

    fun actualizarAlimento(alimento: ComponenteDieta) {
        viewModelScope.launch {
            alimentosRepository.actualizarAlimento(alimento)
        }
    }

    fun eliminarAlimento(alimento: ComponenteDieta) {
        viewModelScope.launch { 
            alimentosRepository.eliminarAlimento(alimento) 
        }
    }

    fun agregarIngrediente(ingrediente: Ingrediente) {
        viewModelScope.launch {
            // Implementar la lógica para agregar ingrediente
        }
    }

    fun obtenerComponentePorId(id: Int): ComponenteDieta? {
        return alimentos.value?.find { it.id == id }
    }
}
