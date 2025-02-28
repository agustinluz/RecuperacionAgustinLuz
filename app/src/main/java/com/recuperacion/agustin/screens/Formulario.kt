package com.recuperacion.agustin.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.recuperacion.agustin.modelo.ComponenteDieta
import com.recuperacion.agustin.modelo.TipoComponente
import com.recuperacion.agustin.modelo.Ingrediente
import com.recuperacion.agustin.viewmodel.AlimentosMVVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Formulario(
    viewModel: AlimentosMVVM,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var tipo by rememberSaveable { mutableStateOf(TipoComponente.SIMPLE) }
    var grHC by rememberSaveable { mutableStateOf("0") }
    var grLip by rememberSaveable { mutableStateOf("0") }
    var grPro by rememberSaveable { mutableStateOf("0") }
    var kcal by rememberSaveable { mutableStateOf("0") }
    var showError by rememberSaveable { mutableStateOf(false) }
    var ingredientes = remember { mutableStateListOf<Ingrediente>() }
    var showIngredientDialog by remember { mutableStateOf(false) }

    // Cálculo automático de calorías
    LaunchedEffect(grHC, grLip, grPro) {
        val hc = grHC.toDoubleOrNull() ?: 0.0
        val lip = grLip.toDoubleOrNull() ?: 0.0
        val pro = grPro.toDoubleOrNull() ?: 0.0

        // Fórmula: HC*4 + Lip*9 + Pro*4
        val calculatedKcal = (hc * 4) + (lip * 9) + (pro * 4)
        kcal = String.format("%.2f", calculatedKcal)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                showError = false
            },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            isError = showError && nombre.isBlank()
        )

        // Selector de tipo de componente
        Column(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Tipo de componente:", style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TipoComponente.values().forEach { tipoComponente ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RadioButton(
                            selected = tipo == tipoComponente,
                            onClick = { tipo = tipoComponente }
                        )
                        Text(tipoComponente.name)
                    }
                }
            }
        }

        // Campos de macronutrientes solo para SIMPLE y PROCESADO
        if (tipo in listOf(TipoComponente.SIMPLE, TipoComponente.PROCESADO)) {
            OutlinedTextField(
                value = grHC,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) grHC = it },
                label = { Text("Gramos HC") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = grLip,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) grLip = it },
                label = { Text("Gramos Lípidos") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = grPro,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) grPro = it },
                label = { Text("Gramos Proteínas") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = kcal,
                onValueChange = { },
                label = { Text("Calorías (calculado automáticamente)") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Sección de ingredientes/componentes
        if (tipo != TipoComponente.SIMPLE) {
            Text(
                text = when (tipo) {
                    TipoComponente.PROCESADO -> "Ingredientes"
                    TipoComponente.RECETA -> "Componentes de la receta"
                    TipoComponente.MENU -> "Platos del menú"
                    TipoComponente.DIETA -> "Componentes de la dieta"
                    else -> "Componentes"
                },
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.titleMedium
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(ingredientes) { ingrediente ->
                    IngredienteItem(
                        ingrediente = ingrediente,
                        onDelete = { ingredientes.remove(ingrediente) }
                    )
                }
            }

            Button(
                onClick = { showIngredientDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
                Spacer(modifier = Modifier.width(8.dp))
                Text(when (tipo) {
                    TipoComponente.PROCESADO -> "Añadir Ingrediente"
                    TipoComponente.RECETA -> "Añadir Componente"
                    TipoComponente.MENU -> "Añadir Plato"
                    TipoComponente.DIETA -> "Añadir Elemento"
                    else -> "Añadir"
                })
            }
        }

        // Botones de acción
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onNavigateBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = {
                    if (nombre.isBlank()) {
                        showError = true
                        return@Button
                    }

                    try {
                        val componente = ComponenteDieta(
                            id = 0,
                            nombre = nombre,
                            tipo = tipo,
                            grHC_ini = if (tipo in listOf(TipoComponente.SIMPLE, TipoComponente.PROCESADO))
                                grHC.toDoubleOrNull() ?: 0.0 else 0.0,
                            grLip_ini = if (tipo in listOf(TipoComponente.SIMPLE, TipoComponente.PROCESADO))
                                grLip.toDoubleOrNull() ?: 0.0 else 0.0,
                            grPro_ini = if (tipo in listOf(TipoComponente.SIMPLE, TipoComponente.PROCESADO))
                                grPro.toDoubleOrNull() ?: 0.0 else 0.0,
                            Kcal_ini = if (tipo in listOf(TipoComponente.SIMPLE, TipoComponente.PROCESADO))
                                kcal.toDoubleOrNull() ?: 0.0 else 0.0
                        )
                        viewModel.agregarAlimentoConIngredientes(componente, ingredientes)
                        onNavigateBack()
                    } catch (e: Exception) {
                        showError = true
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !showError
            ) {
                Text("Guardar")
            }
        }
    }

    if (showIngredientDialog) {
        AgregarIngredienteDialog(
            onDismiss = { showIngredientDialog = false },
            onConfirm = { nombreIngrediente, cantidadIngrediente ->
                ingredientes.add(Ingrediente(0, 0 ,nombreIngrediente, cantidadIngrediente))
                showIngredientDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarIngredienteDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Ingrediente") },
        text = {
            Column {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        showError = false
                    },
                    label = { Text("Nombre") },
                    isError = showError && nombre.isBlank()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) cantidad = it },
                    label = { Text("Cantidad (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = showError && (cantidad.isBlank() || cantidad.toDoubleOrNull() == null)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (nombre.isBlank() || cantidad.isBlank() || cantidad.toDoubleOrNull() == null) {
                        showError = true
                        return@TextButton
                    }
                    onConfirm(nombre, cantidad.toDouble())
                }
            ) {
                Text("Añadir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun IngredienteItem(
    ingrediente: Ingrediente,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = ingrediente.nombre,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Cantidad: ${ingrediente.cantidad}g",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar ingrediente"
                )
            }
        }
    }
}