package com.recuperacion.agustin.modelo

import com.recuperacion.agustin.modelo.ComponenteDieta

data class MenuDieta(
    var nombre: String = "",
    var ingredientes: MutableList<ComponenteDieta> = mutableListOf()
)
