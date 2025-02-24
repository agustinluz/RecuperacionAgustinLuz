package com.recuperacion.agustin.screens

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
            .padding(16.dp)
    ) {
        Text(
            text = "Filtrar por tipo:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            componenteExpandido = if (componenteExpandido == componente) null else componente 
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
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
                                    text = "Tipo: ${componente.tipo}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Row {
                                IconButton(onClick = { onNavigateToEdit(componente) }) {
                                    Icon(Icons.Default.Edit, "Editar")
                                }
                                IconButton(onClick = { viewModel.eliminarAlimento(componente) }) {
                                    Icon(Icons.Default.Delete, "Eliminar")
                                }
                            }
                        }

                        // Mostrar detalles cuando está expandido
                        if (componenteExpandido == componente) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            when (componente.tipo) {
                                TipoComponente.MENU, TipoComponente.DIETA -> {
                                    val componenteConIngredientes by viewModel
                                        .obtenerComponenteConIngredientes(componente.id)
                                        .collectAsState(initial = null)
                                    
                                    Text(
                                        text = "Componentes:",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    componenteConIngredientes?.ingredientes?.forEach { ingrediente ->
                                        Text(
                                            text = "• ${ingrediente.nombre} (${ingrediente.cantidad}g)",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                                else -> {
                                    Text("Información nutricional:")
                                    Text("• Carbohidratos: ${componente.grHC_ini}g")
                                    Text("• Lípidos: ${componente.grLip_ini}g")
                                    Text("• Proteínas: ${componente.grPro_ini}g")
                                    Text("• Calorías totales: ${componente.Kcal_ini} kcal")
                                }
                            }
                        }
                    }
                }
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
