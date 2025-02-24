package com.recuperacion.agustin.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog

import com.recuperacion.agustin.modelo.AlimentosMVVM
import com.recuperacion.agustin.modelo.ComponenteDieta
import com.recuperacion.agustin.modelo.Ingrediente
import com.recuperacion.agustin.modelo.TipoComponente

import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Formulario(
    viewModel: AlimentosMVVM,
    componenteAEditar: ComponenteDieta? = null
) {
    var nombre by remember { mutableStateOf(componenteAEditar?.nombre ?: "") }
    var tipo by remember { mutableStateOf(componenteAEditar?.tipo ?: TipoComponente.SIMPLE) }
    var grHC by remember { mutableStateOf(componenteAEditar?.grHC_ini?.toString() ?: "") }
    var grLip by remember { mutableStateOf(componenteAEditar?.grLip_ini?.toString() ?: "") }
    var grPro by remember { mutableStateOf(componenteAEditar?.grPro_ini?.toString() ?: "") }
    
    // Cálculo automático de calorías
    val kcal = remember(grHC, grLip, grPro) {
        val hc = grHC.toDoubleOrNull() ?: 0.0
        val lip = grLip.toDoubleOrNull() ?: 0.0
        val pro = grPro.toDoubleOrNull() ?: 0.0
        (hc * 4 + lip * 9 + pro * 4).toString()
    }
    
    var mostrarDialogoComponentes by remember { mutableStateOf(false) }
    var mostrarMensajeGuardado by remember { mutableStateOf(false) }
    
    val componentesSeleccionados = remember { 
        mutableStateListOf<ComponenteDieta>().apply {
            if (componenteAEditar?.tipo == TipoComponente.MENU || 
                componenteAEditar?.tipo == TipoComponente.DIETA) {
                // Cargar componentes existentes si es una edición
                // Implementar lógica para cargar los componentes existentes
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (componenteAEditar != null) "Editar Componente" else "Nuevo Componente",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Campos comunes
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de tipo (deshabilitado en modo edición)
        if (componenteAEditar == null) {
            Text("Tipo de Componente:")
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(TipoComponente.values()) { tipoOpcion ->
                    FilterChip(
                        selected = tipo == tipoOpcion,
                        onClick = { tipo = tipoOpcion },
                        label = { Text(tipoOpcion.name) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (tipo) {
            TipoComponente.SIMPLE, TipoComponente.PROCESADO -> {
                CamposNutricionales(
                    grHC = grHC,
                    grLip = grLip,
                    grPro = grPro,
                    kcal = kcal,
                    onGrHCChange = { grHC = it },
                    onGrLipChange = { grLip = it },
                    onGrProChange = { grPro = it }
                )
            }
            TipoComponente.MENU, TipoComponente.DIETA -> {
                SelectorComponentes(
                    componentesSeleccionados = componentesSeleccionados,
                    tipo = tipo,
                    viewModel = viewModel,
                    onMostrarDialogo = { mostrarDialogoComponentes = true }
                )
            }
        }

        // Botón guardar/actualizar
        Button(
            onClick = {
                val nuevoComponente = ComponenteDieta(
                    id = componenteAEditar?.id ?: 0,
                    nombre = nombre,
                    tipo = tipo,
                    grHC_ini = grHC.toDoubleOrNull() ?: 0.0,
                    grLip_ini = grLip.toDoubleOrNull() ?: 0.0,
                    grPro_ini = grPro.toDoubleOrNull() ?: 0.0,
                    Kcal_ini = kcal.toDoubleOrNull() ?: 0.0
                )

                if (componenteAEditar != null) {
                    viewModel.actualizarAlimento(nuevoComponente)
                } else {
                    viewModel.agregarAlimento(nuevoComponente)
                }

                mostrarMensajeGuardado = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(if (componenteAEditar != null) "Actualizar" else "Guardar")
        }
    }

    // Diálogos y mensajes
    MostrarDialogos(
        mostrarDialogoComponentes = mostrarDialogoComponentes,
        onDismissDialogo = { mostrarDialogoComponentes = false },
        mostrarMensajeGuardado = mostrarMensajeGuardado,
        onMensajeGuardadoDismiss = { mostrarMensajeGuardado = false },
        viewModel = viewModel,
        tipo = tipo,
        componentesSeleccionados = componentesSeleccionados
    )
}

@Composable
private fun TipoComponenteSelector(
    tipoSeleccionado: TipoComponente,
    onTipoSelected: (TipoComponente) -> Unit
) {
    Column {
        Text("Tipo de Componente:")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TipoComponente.values().forEach { tipo ->
                Button(
                    onClick = { onTipoSelected(tipo) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (tipo == tipoSeleccionado) 
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(tipo.name)
                }
            }
        }
    }
}

@Composable
private fun CamposNutricionales(
    grHC: String,
    grLip: String,
    grPro: String,
    kcal: String,
    onGrHCChange: (String) -> Unit,
    onGrLipChange: (String) -> Unit,
    onGrProChange: (String) -> Unit
) {
    TextField(
        value = grHC,
        onValueChange = onGrHCChange,
        label = { Text("Carbohidratos (g)") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    TextField(
        value = grLip,
        onValueChange = onGrLipChange,
        label = { Text("Lípidos (g)") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))
    TextField(
        value = grPro,
        onValueChange = onGrProChange,
        label = { Text("Proteínas (g)") },
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioComponenteDieta(
    viewModel: AlimentosMVVM,
    componenteAEditar: ComponenteDieta? = null
) {
    var nombre by remember { mutableStateOf(componenteAEditar?.nombre ?: "") }
    var tipo by remember { mutableStateOf(componenteAEditar?.tipo ?: TipoComponente.SIMPLE) }
    var grHC by remember { mutableStateOf(componenteAEditar?.grHC_ini?.toString() ?: "0.0") }
    var grLip by remember { mutableStateOf(componenteAEditar?.grLip_ini?.toString() ?: "0.0") }
    var grPro by remember { mutableStateOf(componenteAEditar?.grPro_ini?.toString() ?: "0.0") }
    var kcal by remember { mutableStateOf(componenteAEditar?.Kcal_ini?.toString() ?: "0.0") }
    val alimentos by viewModel.alimentos.collectAsState()
    var ingredientesSeleccionados by remember { mutableStateOf<List<ComponenteDieta>>(emptyList()) }
    
    LaunchedEffect(componenteAEditar) {
        if (componenteAEditar?.tipo == TipoComponente.MENU) {
            viewModel.obtenerComponenteConIngredientes(componenteAEditar.id)
                .collect { componenteConIngredientes ->
                    ingredientesSeleccionados = componenteConIngredientes.ingredientes
                        .mapNotNull { ingrediente -> 
                            alimentos.find { it.id == ingrediente.componenteDietaId }
                        }
                }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = if (componenteAEditar != null) "Editar Componente" else "Nuevo Componente",
                style = MaterialTheme.typography.titleLarge
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            // Selector de tipo de componente
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TipoComponente.values().forEach { tipoComponente ->
                    OutlinedButton(
                        onClick = { 
                            tipo = tipoComponente
                            if (tipo == TipoComponente.MENU) {
                                grHC = "0.0"
                                grLip = "0.0"
                                grPro = "0.0"
                                kcal = "0.0"
                            } else {
                                ingredientesSeleccionados = emptyList()
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (tipo == tipoComponente) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        )
                    ) {
                        Text(tipoComponente.name)
                    }
                }
            }

            when (tipo) {
                TipoComponente.MENU -> {
                    Text(
                        text = "Seleccionar ingredientes",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    alimentos
                        .filter { it.tipo != TipoComponente.MENU }
                        .forEach { ingrediente ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = ingredientesSeleccionados.contains(ingrediente),
                                    onCheckedChange = { isChecked ->
                                        ingredientesSeleccionados = if (isChecked) {
                                            ingredientesSeleccionados + ingrediente
                                        } else {
                                            ingredientesSeleccionados - ingrediente
                                        }
                                    }
                                )
                                Text(ingrediente.nombre)
                            }
                        }
                }
                else -> {
                    OutlinedTextField(
                        value = grHC,
                        onValueChange = { grHC = it },
                        label = { Text("Hidratos de Carbono (g)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = grLip,
                        onValueChange = { grLip = it },
                        label = { Text("Lípidos (g)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = grPro,
                        onValueChange = { grPro = it },
                        label = { Text("Proteínas (g)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    val calculatedKcal = (grHC.toDoubleOrNull() ?: 0.0) * 4 +
                                       (grLip.toDoubleOrNull() ?: 0.0) * 9 +
                                       (grPro.toDoubleOrNull() ?: 0.0) * 4
                    kcal = calculatedKcal.toString()

                    Text(
                        text = "Calorías totales: $kcal kcal",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Button(
                onClick = {
                    val componente = ComponenteDieta(
                        id = componenteAEditar?.id ?: 0,
                        nombre = nombre,
                        tipo = tipo,
                        grHC_ini = grHC.toDoubleOrNull() ?: 0.0,
                        grLip_ini = grLip.toDoubleOrNull() ?: 0.0,
                        grPro_ini = grPro.toDoubleOrNull() ?: 0.0,
                        Kcal_ini = kcal.toDoubleOrNull() ?: 0.0
                    )
                    
                    if (componenteAEditar != null) {
                        viewModel.actualizarAlimento(componente)
                    } else {
                        viewModel.agregarAlimento(componente)
                    }
                    
                    if (tipo == TipoComponente.MENU) {
                        ingredientesSeleccionados.forEach { ingrediente ->
                            viewModel.agregarIngrediente(
                                Ingrediente(
                                    nombre = ingrediente.nombre,
                                    componenteDietaId = componente.id,
                                    cantidad = 100.0
                                )
                            )
                        }
                    }

                    // Limpiar campos
                    nombre = ""
                    tipo = TipoComponente.SIMPLE
                    grHC = "0.0"
                    grLip = "0.0"
                    grPro = "0.0"
                    kcal = "0.0"
                    ingredientesSeleccionados = emptyList()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nombre.isNotBlank() && (
                    tipo == TipoComponente.MENU && ingredientesSeleccionados.isNotEmpty() ||
                    tipo != TipoComponente.MENU
                )
            ) {
                Text(if (componenteAEditar != null) "Actualizar" else "Guardar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioIngrediente(viewModel: AlimentosMVVM) {
    var nombre by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("100.0") }
    var componenteSeleccionado by remember { mutableStateOf<ComponenteDieta?>(null) }
    val componentes by viewModel.alimentos.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Nuevo Ingrediente",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = cantidad,
            onValueChange = { cantidad = it },
            label = { Text("Cantidad (g)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Selector de componente actualizado
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = componenteSeleccionado?.nombre ?: "Seleccionar componente",
                onValueChange = { },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.exposedDropdownSize()
            ) {
                componentes.forEach { componente ->
                    DropdownMenuItem(
                        text = { Text(componente.nombre) },
                        onClick = { 
                            componenteSeleccionado = componente
                            expanded = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                componenteSeleccionado?.let { componente ->
                    viewModel.agregarIngrediente(
                        Ingrediente(
                            nombre = nombre,
                            cantidad = cantidad.toDoubleOrNull() ?: 100.0,
                            componenteDietaId = componente.id
                        )
                    )
                    // Limpiar campos
                    nombre = ""
                    cantidad = "100.0"
                    componenteSeleccionado = null
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = nombre.isNotBlank() && componenteSeleccionado != null
        ) {
            Text("Guardar Ingrediente")
        }
    }
}

enum class TipoFormulario(val titulo: String) {
    ComponenteDieta("Componente"),
    Ingrediente("Ingrediente")
}

@Composable
private fun MostrarDialogos(
    mostrarDialogoComponentes: Boolean,
    onDismissDialogo: () -> Unit,
    mostrarMensajeGuardado: Boolean,
    onMensajeGuardadoDismiss: () -> Unit,
    viewModel: AlimentosMVVM,
    tipo: TipoComponente,
    componentesSeleccionados: List<ComponenteDieta>
) {
    if (mostrarDialogoComponentes) {
        val alimentos by viewModel.alimentos.collectAsState(initial = emptyList())
        
        AlertDialog(
            onDismissRequest = onDismissDialogo,
            title = { Text("Seleccionar Componentes") },
            text = {
                LazyColumn {
                    items(alimentos.filter { 
                        when (tipo) {
                            TipoComponente.MENU -> it.tipo == TipoComponente.SIMPLE || 
                                                 it.tipo == TipoComponente.PROCESADO
                            TipoComponente.DIETA -> it.tipo != TipoComponente.DIETA
                            else -> false
                        }
                    }) { componente ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (componente in componentesSeleccionados) {
                                        // Implementar la lógica para quitar el componente
                                    } else {
                                        // Implementar la lógica para agregar el componente
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = componente in componentesSeleccionados,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        // Implementar la lógica para agregar el componente
                                    } else {
                                        // Implementar la lógica para quitar el componente
                                    }
                                }
                            )
                            Text(componente.nombre)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = onDismissDialogo) {
                    Text("Aceptar")
                }
            }
        )
    }

    if (mostrarMensajeGuardado) {
        LaunchedEffect(Unit) {
            delay(2000)
            onMensajeGuardadoDismiss()
        }
        Snackbar(
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Componente guardado correctamente")
        }
    }
}

@Composable
private fun SelectorComponentes(
    componentesSeleccionados: List<ComponenteDieta>,
    tipo: TipoComponente,
    viewModel: AlimentosMVVM,
    onMostrarDialogo: () -> Unit
) {
    Button(
        onClick = { onMostrarDialogo() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Añadir Componentes")
    }

    // Lista de componentes seleccionados
    LazyColumn {
        items(componentesSeleccionados) { componente ->
            Text("${componente.nombre} - ${componente.Kcal_ini} kcal")
        }
    }
}
