package com.recuperacion.agustin.room

import androidx.room.*
import com.recuperacion.agustin.modelo.ComponenteConIngredientes
import com.recuperacion.agustin.modelo.ComponenteDieta
import com.recuperacion.agustin.modelo.TipoComponente
import kotlinx.coroutines.flow.Flow

@Dao
interface ComponenteDietaDao {
    @Query("SELECT * FROM componentes")
    fun getAllComponentes(): Flow<List<ComponenteDieta>>

    @Transaction
    @Query("SELECT * FROM componentes WHERE id = :id")
    fun getComponenteConIngredientes(id: Int): Flow<ComponenteConIngredientes>

    @Query("SELECT * FROM componentes WHERE tipo = :tipo")
    fun getComponentesByTipo(tipo: TipoComponente): Flow<List<ComponenteDieta>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComponente(componente: ComponenteDieta): Long

    @Update
    suspend fun updateComponente(componente: ComponenteDieta)

    @Delete
    suspend fun deleteComponente(componente: ComponenteDieta)
}
