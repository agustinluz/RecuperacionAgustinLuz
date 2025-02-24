package com.recuperacion.agustin.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ListadoDetalle(viewModel: AlimentosMVVM, onNavigateToEdit: (ComponenteDieta) -> Unit) {
    var tipoSeleccionado by remember { mutableStateOf<TipoComponente?>(null) }
    val alimentos by viewModel.alimentos.collectAsState(initial = emptyList())
    var componenteExpandido by remember { mutableStateOf<ComponenteDieta?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sección de filtros
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Filtrar por tipo",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = tipoSeleccionado == null,
                        onClick = { tipoSeleccionado = null },
                        label = { Text("Todos") }
                    )
                    TipoComponente.values().forEach { tipo ->
                        FilterChip(
                            selected = tipoSeleccionado == tipo,
                            onClick = { tipoSeleccionado = tipo },
                            label = { Text(tipo.name) }
                        )
                    }
                }
            }
        }

        // Lista de componentes
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val alimentosFiltrados = if (tipoSeleccionado == null) {
                alimentos
            } else {
                alimentos.filter { it.tipo == tipoSeleccionado }
            }

            items(alimentosFiltrados) { componente ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            componenteExpandido = if (componenteExpandido == componente) null else componente 
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = componente.nombre,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = componente.tipo.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Row {
                                IconButton(onClick = { onNavigateToEdit(componente) }) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Editar",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { viewModel.eliminarAlimento(componente) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }

                        if (componenteExpandido == componente) {
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            DetallesComponente(componente, viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetallesComponente(
    componente: ComponenteDieta,
    viewModel: AlimentosMVVM
) {
    when (componente.tipo) {
        TipoComponente.MENU, TipoComponente.DIETA -> {
            val componenteConIngredientes by viewModel
                .obtenerComponenteConIngredientes(componente.id)
                .collectAsState(initial = null)

            Text(
                text = "Componentes",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            componenteConIngredientes?.ingredientes?.forEach { ingrediente ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "• ${ingrediente.nombre}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${ingrediente.cantidad}g",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
        else -> {
            InfoNutricional(componente)
        }
    }
}

@Composable
private fun InfoNutricional(componente: ComponenteDieta) {
    Column {
        Text(
            text = "Información nutricional",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            InfoNutricionalItem("Carbohidratos", componente.grHC_ini)
            InfoNutricionalItem("Lípidos", componente.grLip_ini)
            InfoNutricionalItem("Proteínas", componente.grPro_ini)
        }
        Text(
            text = "Calorías totales: ${componente.Kcal_ini} kcal",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun InfoNutricionalItem(label: String, valor: Double) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "${valor}g",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
