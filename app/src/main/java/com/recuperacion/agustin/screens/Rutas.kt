package com.recuperacion.agustin.screens
sealed class Ruta(val ruta: String) {
    object Formulario : Ruta("formulario")
    object ListadoDetalle : Ruta("listas")
}