package com.recuperacion.agustin.componentes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiTopAppBar(onNavigateToFormulario: () -> Unit, onNavigateToListado: () -> Unit) {
    TopAppBar(
        title = { Text("Mi Aplicación") },
        actions = {
            IconButton(onClick = onNavigateToFormulario) {
                Icon(Icons.Filled.Menu, contentDescription = "Formulario")
            }
            IconButton(onClick = onNavigateToListado) {
                Icon(Icons.Filled.List, contentDescription = "Listado")
            }
        }
    )
}