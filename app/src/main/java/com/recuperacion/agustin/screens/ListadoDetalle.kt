package com.recuperacion.agustin.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.recuperacion.agustin.componentes.ComponenteItem
import com.recuperacion.agustin.modelo.AlimentosMVVM
import com.recuperacion.agustin.modelo.ComponenteDieta
import com.recuperacion.agustin.modelo.TipoComponente
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ListadoDetalle(viewModel: AlimentosMVVM, onNavigateToEdit: (ComponenteDieta) -> Unit) {
    var tipoSeleccionado by remember { mutableStateOf<TipoComponente?>(null) }
    val alimentos by viewModel.alimentos.collectAsState(initial = emptyList())
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Filtrar por tipo:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Botones de filtro en un FlowRow para mejor adaptabilidad
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FiltroBotón(
                texto = "Todos",
                seleccionado = tipoSeleccionado == null,
                onClick = { tipoSeleccionado = null }
            )
            TipoComponente.values().forEach { tipo ->
                FiltroBotón(
                    texto = tipo.name,
                    seleccionado = tipoSeleccionado == tipo,
                    onClick = { tipoSeleccionado = tipo }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val alimentosFiltrados = if (tipoSeleccionado == null) {
                alimentos
            } else {
                alimentos.filter { it.tipo == tipoSeleccionado }
            }

            items(alimentosFiltrados) { componente ->
                ComponenteItem(
                    componente = componente,
                    onEdit = { onNavigateToEdit(it) },
                    onDelete = { viewModel.eliminarAlimento(it) }
                )
            }
        }
    }
}

@Composable
private fun FiltroBotón(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (seleccionado) MaterialTheme.colorScheme.primary 
                           else MaterialTheme.colorScheme.secondary
        ),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(texto)
    }
}
