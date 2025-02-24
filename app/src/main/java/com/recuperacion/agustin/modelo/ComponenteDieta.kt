package com.recuperacion.agustin.modelo

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.recuperacion.agustin.modelo.TipoComponente
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "componentes_dieta")
data class ComponenteDieta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val tipo: TipoComponente,
    val grHC_ini: Double,
    val grLip_ini: Double,
    val grPro_ini: Double,
    val Kcal_ini: Double
) : Parcelable {

    fun calcularKcal(): Double {
        return (grHC_ini * 4) + (grLip_ini * 9) + (grPro_ini * 4)
    }

}