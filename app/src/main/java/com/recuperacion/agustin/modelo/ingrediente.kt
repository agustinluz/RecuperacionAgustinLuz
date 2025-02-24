package com.recuperacion.agustin.modelo

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "ingredientes",
    foreignKeys = [
        ForeignKey(
            entity = ComponenteDieta::class,
            parentColumns = ["id"],
            childColumns = ["componenteDietaId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Ingrediente(
    @PrimaryKey(autoGenerate = true) 
    val id: Int = 0,
    val componenteDietaId: Int,
    val nombre: String,
    val cantidad: Double = 100.0
) : Serializable
