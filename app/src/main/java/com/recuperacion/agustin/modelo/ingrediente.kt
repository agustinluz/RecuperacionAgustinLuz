package com.recuperacion.agustin.modelo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "ingredientes")
data class Ingrediente(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val componenteDietaId: Int,
    val nombre: String,
    val cantidad: Double = 100.0
) : Serializable
