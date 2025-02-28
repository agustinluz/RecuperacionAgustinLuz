package com.recuperacion.agustin.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.recuperacion.agustin.modelo.ComponenteDieta
import com.recuperacion.agustin.modelo.TipoComponente
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.recuperacion.agustin.viewmodel.AlimentosMVVM

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ListadoDetalle(viewModel: AlimentosMVVM, onNavigateToFormulario: () -> Unit, modifier: Modifier) {
    val alimentos by viewModel.allComponentes.collectAsState(initial = emptyList())
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        alimentos.forEach { componente ->
            ComponenteItem(
                componente = componente,
                onVerDetalles = {  },
                onEliminar = {  }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ComponenteItem(
    componente: ComponenteDieta,
    onVerDetalles: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = componente.nombre)
            Text(text = "Tipo: ${componente.tipo}")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onVerDetalles) {
                    Text("Ver detalles")
                }
                Button(onClick = onEliminar) {
                    Text("Eliminar")
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
                .getComponenteConIngredientes(componente.id)
                .collectAsState(initial = null)

            Column {
                Text(
                    text = "Componentes:",
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
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Calorías totales: ${componenteConIngredientes?.calcularKcalTotales()?.toInt() ?: 0} kcal",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
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
