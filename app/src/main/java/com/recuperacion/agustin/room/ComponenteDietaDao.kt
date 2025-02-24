package com.recuperacion.agustin.room

import androidx.room.*
import com.recuperacion.agustin.modelo.ComponenteDieta
import com.recuperacion.agustin.modelo.ComponenteConIngredientes
import kotlinx.coroutines.flow.Flow

@Dao
interface ComponenteDietaDao {

    @Query("SELECT * FROM componentes_dieta ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<ComponenteDieta>>

    @Transaction
    @Query("SELECT * FROM componentes_dieta WHERE id = :componenteId")
    fun obtenerComponenteConIngredientes(componenteId: Int): Flow<ComponenteConIngredientes>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(alimento: ComponenteDieta)

    @Update
    suspend fun actualizar(alimento: ComponenteDieta)

    @Delete
    suspend fun eliminar(alimento: ComponenteDieta)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarYObtenerIdAlimento(alimento: ComponenteDieta): Long
}
