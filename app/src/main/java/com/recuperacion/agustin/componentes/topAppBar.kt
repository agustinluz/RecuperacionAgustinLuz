package com.recuperacion.agustin.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiTopAppBar(onNavigateToFormulario: () -> Unit, onNavigateToListado: () -> Unit) {
    TopAppBar(
        title = { Text("Mi Aplicación") },
        actions = {
            IconButton(onClick = onNavigateToFormulario) {
                Icon(Icons.Default.Menu, contentDescription = "Formulario")
            }
            IconButton(onClick = onNavigateToListado) {
                Icon(Icons.Default.List, contentDescription = "Listado")
            }
        }
    )
}
