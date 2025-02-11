package com.recuperacion.agustin.screens

import ComponenteDieta
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import com.recuperacion.agustin.modelo.AlimentosMVVM


import kotlinx.coroutines.launch

@Composable
fun Formulario(viewModel: AlimentosMVVM) {
    var alimento by rememberSaveable { mutableStateOf(ComponenteDieta()) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() } // Para mostrar mensajes
    val coroutineScope = rememberCoroutineScope()


    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = alimento.nombre,
            onValueChange = { newText ->
                alimento = alimento.copy(nombre = newText)
            },
            label = { Text("Nombre del alimento") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = alimento.grPro_ini.toString(),
            onValueChange = { newText ->
                alimento = alimento.copy(grPro_ini = newText.toDoubleOrNull() ?: 0.0)
            },
            label = { Text("Proteínas (gr)") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = alimento.grHC_ini.toString(),
            onValueChange = { newText ->
                alimento = alimento.copy(grHC_ini = newText.toDoubleOrNull() ?: 0.0)
            },
            label = { Text("Carbohidratos (gr)") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = alimento.grLip_ini.toString(),
            onValueChange = { newText ->
                alimento = alimento.copy(grLip_ini = newText.toDoubleOrNull() ?: 0.0)
            },
            label = { Text("Lípidos (gr)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Botón Guardar
        Button(
            onClick = {
                if (alimento.nombre.isNotEmpty()) {
                    viewModel.agregarAlimento(alimento)
                    alimento = ComponenteDieta()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Alimento guardado correctamente")
                    }
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("El nombre no puede estar vacío")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }

        // Snackbar para feedback visual
        SnackbarHost(hostState = snackbarHostState)
    }
}
