package com.recuperacion.agustin.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.recuperacion.agustin.componentes.EditarAlimentoDialog
import com.recuperacion.agustin.modelo.AlimentosMVVM
import com.recuperacion.agustin.modelo.ComponenteDieta
import com.recuperacion.agustin.modelo.TipoComponente


@Composable
fun ListadoDetalle(viewModel: AlimentosMVVM) {
    val alimentos by viewModel.alimentos.collectAsState()
    var filtroTipo by remember { mutableStateOf<TipoComponente?>(null) }
    var alimentoSeleccionado by remember { mutableStateOf<ComponenteDieta?>(null) }

    Column {
        // Filtro por tipo de alimento
        Row(modifier = Modifier.padding(16.dp)) {
            Button(onClick = { filtroTipo = null }) {
                Text("Todos")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { filtroTipo = TipoComponente.SIMPLE }) {
                Text("Simples")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { filtroTipo = TipoComponente.MENU }) {
                Text("Menú")
            }
        }

        val alimentosFiltrados = alimentos.filter { filtroTipo == null || it.tipo == filtroTipo }

        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(alimentosFiltrados) { alimento ->
                Card(
                    border = BorderStroke(2.dp, Color.Blue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { alimentoSeleccionado = alimento }
                ) {
                    Text(
                        text = "${alimento.nombre}, ${alimento.Kcal_ini} KCal",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // Muestra el diálogo de edición si hay un alimento seleccionado
    alimentoSeleccionado?.let { alimento ->
        EditarAlimentoDialog(
            alimento = alimento,
            onGuardar = { nuevoAlimento ->
                viewModel.actualizarAlimento(nuevoAlimento)
                alimentoSeleccionado = null
            },
            onBorrar = {
                viewModel.eliminarAlimento(alimento)
                alimentoSeleccionado = null
            },
            onDismiss = { alimentoSeleccionado = null }
        )
    }
}
