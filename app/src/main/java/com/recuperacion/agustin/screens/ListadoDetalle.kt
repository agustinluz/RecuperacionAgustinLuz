package com.recuperacion.agustin.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.recuperacion.agustin.modelo.ComponenteDieta
import com.recuperacion.agustin.modelo.TipoComponente
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import com.recuperacion.agustin.modelo.ComponenteConIngredientes
import com.recuperacion.agustin.modelo.Ingrediente
import com.recuperacion.agustin.viewmodel.AlimentosMVVM

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ListadoDetalle(
    viewModel: AlimentosMVVM,
    onNavigateToFormulario: () -> Unit,
    onNavigateToEdit: (ComponenteDieta) -> Unit,
    modifier: Modifier = Modifier
) {
    var tipoFiltrado by remember { mutableStateOf<TipoComponente?>(null) }
    val alimentos by viewModel.allComponentes.collectAsState(initial = emptyList())

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToFormulario,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir componente")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Filtros rápidos
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item{

                    FilterChip(
                        selected = tipoFiltrado == null,
                        onClick = { tipoFiltrado = null },
                        label = { Text("Todos") }
                    )
                    FilterChip(
                        selected = tipoFiltrado == TipoComponente.SIMPLE,
                        onClick = { tipoFiltrado = TipoComponente.SIMPLE },
                        label = { Text("Simples") }
                    )
                    FilterChip(
                        selected = tipoFiltrado == TipoComponente.PROCESADO,
                        onClick = { tipoFiltrado = TipoComponente.PROCESADO },
                        label = { Text("Procesados") }
                    )
                    FilterChip(
                        selected = tipoFiltrado == TipoComponente.RECETA,
                        onClick = { tipoFiltrado = TipoComponente.RECETA },
                        label = { Text("Recetas") }
                    )
                    FilterChip(
                        selected = tipoFiltrado == TipoComponente.MENU,
                        onClick = { tipoFiltrado = TipoComponente.MENU },
                        label = { Text("Menus") }
                    )
                    FilterChip(
                        selected = tipoFiltrado == TipoComponente.DIETA,
                        onClick = { tipoFiltrado = TipoComponente.DIETA },
                        label = { Text("Dietas") }
                    )
                }
            }

            // Lista filtrada
            val componentesFiltrados = remember(alimentos, tipoFiltrado) {
                if (tipoFiltrado == null) alimentos
                else alimentos.filter { it.tipo == tipoFiltrado }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(componentesFiltrados) { componente ->
                    ComponenteItem(
                        componente = componente,
                        viewModel = viewModel,
                        onEditar = { onNavigateToEdit(componente) },
                        onEliminar = { viewModel.eliminarComponente(componente) }
                    )
                }
            }
        }
    }
}

@Composable
fun ComponenteItem(
    componente: ComponenteDieta,
    viewModel: AlimentosMVVM,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    var expandido by remember { mutableStateOf(false) }
    val componenteConIngredientes by viewModel.getComponenteConIngredientes(componente.id)
        .collectAsState(initial = null)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = componente.nombre,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Tipo: ${componente.tipo}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        IconButton(
                            onClick = onEditar,
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.Edit, "Editar")
                        }
                    }
                    item {
                        IconButton(
                            onClick = onEliminar,
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Delete, "Eliminar")
                        }
                    }
                    item {
                        IconButton(
                            onClick = { expandido = !expandido },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                if (expandido) Icons.Default.KeyboardArrowUp else Icons.Default.Build,
                                if (expandido) "Contraer" else "Expandir"
                            )
                        }
                    }
                }
            }

            if (expandido) {
                Spacer(modifier = Modifier.height(8.dp))
                when (componente.tipo) {
                    TipoComponente.SIMPLE, TipoComponente.PROCESADO -> {
                        InfoNutricional(componente)
                    }
                    else -> {
                        componenteConIngredientes?.let { compConIng ->
                            ListaComponentes(compConIng.ingredientes)
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            ResumenNutricional(compConIng)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ListaComponentes(ingredientes: List<Ingrediente>) {
    Text(
        text = "Componentes:",
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(bottom = 4.dp)
    )
    ingredientes.forEach { ingrediente ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "• ${ingrediente.nombre}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${ingrediente.cantidad}g",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ResumenNutricional(componenteConIngredientes: ComponenteConIngredientes) {
    val macros = componenteConIngredientes.calcularMacronutrientesTotales()
    val calorias = componenteConIngredientes.calcularKcalTotales()

    Column {
        Text(
            text = "Valores nutricionales totales:",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NutrienteItem("Carbohidratos", macros.first)
            NutrienteItem("Lípidos", macros.second)
            NutrienteItem("Proteínas", macros.third)
        }
        Text(
            text = "Calorías totales: ${String.format("%.1f", calorias)} kcal",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun InfoNutricional(componente: ComponenteDieta) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Información nutricional:",
            style = MaterialTheme.typography.titleSmall
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NutrienteItem("Carbohidratos", componente.grHC_ini)
            NutrienteItem("Lípidos", componente.grLip_ini)
            NutrienteItem("Proteínas", componente.grPro_ini)
        }
        
        // Cálculo de calorías totales
        val caloriasCalculadas = (componente.grHC_ini * 4) + 
                               (componente.grPro_ini * 4) + 
                               (componente.grLip_ini * 9)
        
        Text(
            text = "Calorías calculadas: ${String.format("%.1f", caloriasCalculadas)} kcal",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun NutrienteItem(nombre: String, valor: Double) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = nombre,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "${String.format("%.1f", valor)}g",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
