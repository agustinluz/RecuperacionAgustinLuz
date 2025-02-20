package com.recuperacion.agustin.room

import androidx.room.*
import com.recuperacion.agustin.modelo.ComponenteDieta
import kotlinx.coroutines.flow.Flow

@Dao
interface ComponenteDietaDao {

    @Query("SELECT * FROM componentes_dieta ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<ComponenteDieta>> // ✅ Agregada consulta para obtener los datos

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(alimento: ComponenteDieta)

    @Update
    suspend fun actualizar(alimento: ComponenteDieta)

    @Delete
    suspend fun eliminar(alimento: ComponenteDieta)
}
