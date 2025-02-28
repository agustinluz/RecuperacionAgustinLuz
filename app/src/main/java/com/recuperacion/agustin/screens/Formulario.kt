package com.recuperacion.agustin.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.recuperacion.agustin.modelo.ComponenteConIngredientes
import com.recuperacion.agustin.modelo.ComponenteDieta
import com.recuperacion.agustin.modelo.TipoComponente
import com.recuperacion.agustin.modelo.Ingrediente
import com.recuperacion.agustin.viewmodel.AlimentosMVVM
import com.recuperacion.agustin.components.TipoSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Formulario(
    viewModel: AlimentosMVVM,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val componenteToEdit by viewModel.componenteToEdit.collectAsState()
    var nombre by rememberSaveable { mutableStateOf(componenteToEdit?.nombre ?: "") }
    var selectedTipo by rememberSaveable { mutableStateOf(componenteToEdit?.tipo ?: TipoComponente.SIMPLE) }
    var grHC by rememberSaveable { mutableStateOf(componenteToEdit?.grHC_ini?.toString() ?: "0") }
    var grLip by rememberSaveable { mutableStateOf(componenteToEdit?.grLip_ini?.toString() ?: "0") }
    var grPro by rememberSaveable { mutableStateOf(componenteToEdit?.grPro_ini?.toString() ?: "0") }
    var kcal by rememberSaveable { mutableStateOf(componenteToEdit?.Kcal_ini?.toString() ?: "0") }
    var ingredientes = remember { mutableStateListOf<Ingrediente>() }
    var showIngredientDialog by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    // Cargar ingredientes si estamos editando
    LaunchedEffect(componenteToEdit) {
        if (componenteToEdit != null) {
            viewModel.getComponenteConIngredientes(componenteToEdit!!.id)
                .collect { componenteConIngredientes ->
                    ingredientes.clear()
                    ingredientes.addAll(componenteConIngredientes.ingredientes)
                }
        }
    }

    // Cálculo automático de calorías
    LaunchedEffect(grHC, grLip, grPro) {
        val hc = grHC.toDoubleOrNull() ?: 0.0
        val lip = grLip.toDoubleOrNull() ?: 0.0
        val pro = grPro.toDoubleOrNull() ?: 0.0

        // Fórmula: HC*4 + Lip*9 + Pro*4
        val calculatedKcal = (hc * 4) + (lip * 9) + (pro * 4)
        kcal = String.format("%.2f", calculatedKcal)
    }

    // Limpiar componenteToEdit al salir
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearComponenteToEdit()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (componenteToEdit == null) "Nuevo Componente" else "Editar Componente") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
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

            TipoSelector(
                selectedTipo = selectedTipo,
                onTipoSelected = { selectedTipo = it }
            )

            // Campos de macronutrientes solo para SIMPLE y PROCESADO
            if (selectedTipo in listOf(TipoComponente.SIMPLE, TipoComponente.PROCESADO)) {
                MacronutrientesForm(
                    grHC = grHC,
                    onGrHCChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) grHC = it },
                    grLip = grLip,
                    onGrLipChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) grLip = it },
                    grPro = grPro,
                    onGrProChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) grPro = it },
                    kcal = kcal
                )
            }

            // Sección de componentes para recetas, menús y dietas
            if (selectedTipo in listOf(TipoComponente.RECETA, TipoComponente.MENU, TipoComponente.DIETA)) {
                Text(
                    text = when (selectedTipo) {
                        TipoComponente.RECETA -> "Componentes de la receta"
                        TipoComponente.MENU -> "Platos del menú"
                        TipoComponente.DIETA -> "Componentes de la dieta"
                        else -> ""
                    },
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(ingredientes) { ingrediente ->
                        ComponenteSeleccionadoItem(
                            ingrediente = ingrediente,
                            onDelete = { ingredientes.remove(ingrediente) }
                        )
                    }
                }

                OutlinedButton(
                    onClick = { showIngredientDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Añadir Componente")
                }

                if (ingredientes.isNotEmpty()) {
                    val componenteConIngredientes = ComponenteConIngredientes(
                        componente = ComponenteDieta(tipo = selectedTipo),
                        ingredientes = ingredientes
                    )
                    ResumenNutricional(componenteConIngredientes)
                }
            }

            // Botones de acción al final
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Cancelar
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                // Botón Guardar
                Button(
                    onClick = {
                        if (nombre.isBlank()) {
                            showError = true
                            return@Button
                        }

                        try {
                            val componente = ComponenteDieta(
                                id = componenteToEdit?.id ?: 0,
                                nombre = nombre,
                                tipo = selectedTipo,
                                grHC_ini = if (selectedTipo in listOf(TipoComponente.SIMPLE, TipoComponente.PROCESADO))
                                    grHC.toDoubleOrNull() ?: 0.0 else 0.0,
                                grLip_ini = if (selectedTipo in listOf(TipoComponente.SIMPLE, TipoComponente.PROCESADO))
                                    grLip.toDoubleOrNull() ?: 0.0 else 0.0,
                                grPro_ini = if (selectedTipo in listOf(TipoComponente.SIMPLE, TipoComponente.PROCESADO))
                                    grPro.toDoubleOrNull() ?: 0.0 else 0.0,
                                Kcal_ini = if (selectedTipo in listOf(TipoComponente.SIMPLE, TipoComponente.PROCESADO))
                                    kcal.toDoubleOrNull() ?: 0.0 else 0.0
                            )
                            
                            if (componenteToEdit == null) {
                                viewModel.agregarAlimentoConIngredientes(componente, ingredientes)
                            } else {
                                viewModel.actualizarAlimentoConIngredientes(componente, ingredientes)
                            }
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
    }

    // Diálogo para añadir componentes
    if (showIngredientDialog) {
        SeleccionarComponenteDialog(
            tipo = selectedTipo,
            viewModel = viewModel,
            componentesActuales = ingredientes.map { it.componenteId },
            onDismiss = { showIngredientDialog = false },
            onConfirm = { componente, cantidad ->
                ingredientes.add(
                    Ingrediente(
                        id = 0,
                        componenteId = componente.id,
                        nombre = componente.nombre,
                        cantidad = cantidad
                    )
                )
            }
        )
    }
}

@Composable
private fun MacronutrientesForm(
    grHC: String,
    onGrHCChange: (String) -> Unit,
    grLip: String,
    onGrLipChange: (String) -> Unit,
    grPro: String,
    onGrProChange: (String) -> Unit,
    kcal: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = grHC,
            onValueChange = onGrHCChange,
            label = { Text("Gramos HC") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = grLip,
            onValueChange = onGrLipChange,
            label = { Text("Gramos Lípidos") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = grPro,
            onValueChange = onGrProChange,
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
}

@Composable
private fun ComponenteSeleccionadoItem(
    ingrediente: Ingrediente,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = ingrediente.nombre,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${ingrediente.cantidad}g",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Eliminar",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionarComponenteDialog(
    tipo: TipoComponente,
    viewModel: AlimentosMVVM,
    componentesActuales: List<Int>,
    onDismiss: () -> Unit,
    onConfirm: (ComponenteDieta, Double) -> Unit
) {
    var componenteSeleccionado by remember { mutableStateOf<ComponenteDieta?>(null) }
    var cantidad by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    
    val componentesPermitidos by viewModel.allComponentes.collectAsState(initial = emptyList())
    val componentesFiltrados = remember(componentesPermitidos, tipo, componentesActuales) {
        componentesPermitidos.filter { componente ->
            componente.id !in componentesActuales && when (tipo) {
                TipoComponente.RECETA -> componente.tipo in listOf(TipoComponente.SIMPLE, TipoComponente.PROCESADO)
                TipoComponente.MENU -> componente.tipo in listOf(TipoComponente.SIMPLE, TipoComponente.PROCESADO, TipoComponente.RECETA)
                TipoComponente.DIETA -> componente.tipo in listOf(TipoComponente.SIMPLE, TipoComponente.PROCESADO, TipoComponente.RECETA, TipoComponente.MENU)
                else -> false
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Componente") },
        text = {
            Column {
                LazyColumn(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                ) {
                    items(componentesFiltrados) { componente ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { componenteSeleccionado = componente }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = componenteSeleccionado == componente,
                                onClick = { componenteSeleccionado = componente }
                            )
                            Column(modifier = Modifier.padding(start = 8.dp)) {
                                Text(componente.nombre)
                                Text(
                                    "${componente.tipo}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = cantidad,
                    onValueChange = { 
                        if (it.isEmpty() || it.toDoubleOrNull() != null) {
                            cantidad = it
                            showError = false
                        }
                    },
                    label = { Text("Cantidad (g)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = showError && (cantidad.isBlank() || cantidad.toDoubleOrNull() == null),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (componenteSeleccionado == null || cantidad.isBlank() || cantidad.toDoubleOrNull() == null) {
                        showError = true
                        return@TextButton
                    }
                    onConfirm(componenteSeleccionado!!, cantidad.toDouble())
                    onDismiss()
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