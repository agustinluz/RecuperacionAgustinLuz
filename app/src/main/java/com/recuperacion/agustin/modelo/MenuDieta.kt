package com.recuperacion.agustin.modelo

import ComponenteDieta

data class MenuDieta(
    var nombre: String = "",
    var ingredientes: MutableList<ComponenteDieta> = mutableListOf()
)