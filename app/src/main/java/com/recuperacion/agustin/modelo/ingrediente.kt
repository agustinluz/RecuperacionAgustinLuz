package com.recuperacion.agustin.modelo
//
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index
import java.io.Serializable
@Entity(
    tableName = "ingredientes",
    foreignKeys = [
        ForeignKey(
            entity = ComponenteDieta::class,
            parentColumns = ["id"],
            childColumns = ["componenteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("componenteId")]
)
data class Ingrediente(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var componenteId: Int,
    val nombre: String,
    val cantidad: Double
) : Serializable
