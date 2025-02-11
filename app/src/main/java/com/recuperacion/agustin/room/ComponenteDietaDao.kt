package com.recuperacion.agustin.room

import ComponenteDieta
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ComponenteDietaDao {

    @Query("SELECT * FROM componentes_dieta ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<ComponenteDieta>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(alimento: ComponenteDieta)

    @Update
    suspend fun actualizar(alimento: ComponenteDieta)

    @Delete
    suspend fun eliminar(alimento: ComponenteDieta)
}
