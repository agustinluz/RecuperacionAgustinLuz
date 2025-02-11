package com.recuperacion.agustin.modelo

import ComponenteDieta
import com.recuperacion.agustin.room.ComponenteDietaDao
import kotlinx.coroutines.flow.Flow

class AlimentosRepository(private val componenteDietaDao: ComponenteDietaDao) {

    // Flow para obtener los alimentos en tiempo real
    val alimentos: Flow<List<ComponenteDieta>> = componenteDietaDao.obtenerTodos()

    suspend fun agregarAlimento(alimento: ComponenteDieta) {
        componenteDietaDao.insertar(alimento)
    }

    suspend fun actualizarAlimento(alimento: ComponenteDieta) {
        componenteDietaDao.actualizar(alimento)
    }

    suspend fun eliminarAlimento(alimento: ComponenteDieta) {
        componenteDietaDao.eliminar(alimento)
    }
}
