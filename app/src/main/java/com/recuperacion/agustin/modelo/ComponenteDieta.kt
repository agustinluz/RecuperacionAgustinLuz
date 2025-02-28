package com.recuperacion.agustin.modelo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "componentes")
data class ComponenteDieta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String = "",
    val tipo: TipoComponente = TipoComponente.SIMPLE,
    val grHC_ini: Double = 0.0,
    val grLip_ini: Double = 0.0,
    val grPro_ini: Double = 0.0,
    val Kcal_ini: Double = 0.0
) : Serializable {
    fun calcularCalorias(cantidad: Double): Double {
        return (Kcal_ini * cantidad) / 100.0
    }

    fun calcularMacronutrientes(cantidad: Double): Triple<Double, Double, Double> {
        val factor = cantidad / 100.0
        return Triple(
            grHC_ini * factor,
            grLip_ini * factor,
            grPro_ini * factor
        )
    }

    fun puedeContener(otroComponente: ComponenteDieta): Boolean {
        return when (tipo) {
            TipoComponente.SIMPLE, TipoComponente.PROCESADO -> false
            TipoComponente.RECETA -> otroComponente.tipo in listOf(TipoComponente.SIMPLE, TipoComponente.PROCESADO)
            TipoComponente.MENU -> otroComponente.tipo in listOf(TipoComponente.SIMPLE, TipoComponente.PROCESADO, TipoComponente.RECETA)
            TipoComponente.DIETA -> otroComponente.tipo in listOf(TipoComponente.SIMPLE, TipoComponente.PROCESADO, TipoComponente.RECETA, TipoComponente.MENU)
        }
    }
}