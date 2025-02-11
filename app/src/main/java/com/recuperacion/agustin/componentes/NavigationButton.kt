package com.recuperacion.agustin.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun NavigationButton(
    onNavigateToFormulario: () -> Unit,
    onNavigateToListado: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = onNavigateToFormulario) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Formulario",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Formulario")
        }
        Button(onClick = onNavigateToListado) {
            Icon(
                imageVector = Icons.Filled.List,
                contentDescription = "Listado",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Listado")
        }
    }
}
