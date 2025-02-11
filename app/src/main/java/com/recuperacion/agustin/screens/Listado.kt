package com.recuperacion.agustin.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.recuperacion.agustin.modelo.AlimentosMVVM



@Composable
fun ListadoDetalle(viewModel: AlimentosMVVM) {
    val alimentos by viewModel.alimentos.collectAsState()

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(alimentos) { alimento ->
            Card(
                border = BorderStroke(2.dp, Color.Blue),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        //viewModel.eliminarAlimento(alimento)

                    }
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
