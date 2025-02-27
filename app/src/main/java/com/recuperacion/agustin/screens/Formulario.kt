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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.material3.Divider
import com.recuperacion.agustin.modelo.AlimentosMVVM
import com.recuperacion.agustin.modelo.ComponenteDieta
import com.recuperacion.agustin.modelo.Ingrediente
import com.recuperacion.agustin.modelo.TipoComponente
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
    val scope = rememberCoroutineScope()
    
    val componentesSeleccionados = remember { mutableStateListOf<ComponenteDieta>() }

    // Cargar componentes existentes si es una edición
    val componenteConIngredientes by viewModel
        .obtenerComponenteConIngredientes(componenteAEditar?.id ?: 0)
        .collectAsState(initial = null)

    LaunchedEffect(componenteAEditar) {
        if (componenteAEditar != null) {
            tipo = componenteAEditar.tipo
            nombre = componenteAEditar.nombre
            grHC = componenteAEditar.grHC_ini.toString()
            grLip = componenteAEditar.grLip_ini.toString()
            grPro = componenteAEditar.grPro_ini.toString()
        }
    }

    // Función para limpiar el formulario
    fun limpiarFormulario() {
        nombre = ""
        tipo = TipoComponente.SIMPLE
        grHC = ""
        grLip = ""
        grPro = ""
        componentesSeleccionados.clear()
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
                Column {
                    // Mostrar componentes existentes
                    if (componenteAEditar != null) {
                        Text(
                            text = "Componentes actuales:",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        componenteConIngredientes?.ingredientes?.forEach { ingrediente ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ingrediente.nombre,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { 
                                        viewModel.eliminarIngrediente(ingrediente)
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Eliminar componente"
                                    )
                                }
                            }
                            Divider()
                        }
                    }

                    // Botón para añadir nuevos componentes
                    Button(
                        onClick = { mostrarDialogoComponentes = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text("Añadir Componentes")
                    }

                    // Lista de componentes seleccionados nuevos
                    if (componentesSeleccionados.isNotEmpty()) {
                        Text(
                            text = "Nuevos componentes a añadir:",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        componentesSeleccionados.forEach { componente ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = componente.nombre,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { 
                                        componentesSeleccionados.remove(componente)
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Eliminar de la selección"
                                    )
                                }
                            }
                            Divider()
                        }
                    }
                }
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
                    viewModel.actualizarAlimentoConIngredientes(
                        nuevoComponente,
                        componentesSeleccionados.toList()
                    )
                } else {
                    viewModel.agregarAlimentoConIngredientes(
                        nuevoComponente,
                        componentesSeleccionados.toList()
                    )
                }
                
                mostrarMensajeGuardado = true
                limpiarFormulario()
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
        onMensajeGuardadoDismiss = { 
            mostrarMensajeGuardado = false
        },
        viewModel = viewModel,
        tipo = tipo,
        componentesSeleccionados = componentesSeleccionados,
        onComponenteSeleccionado = { componente, seleccionado ->
            if (seleccionado) {
                componentesSeleccionados.add(componente)
            } else {
                componentesSeleccionados.remove(componente)
            }
        }
    )
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

@Composable
private fun MostrarDialogos(
    mostrarDialogoComponentes: Boolean,
    onDismissDialogo: () -> Unit,
    mostrarMensajeGuardado: Boolean,
    onMensajeGuardadoDismiss: () -> Unit,
    viewModel: AlimentosMVVM,
    tipo: TipoComponente,
    componentesSeleccionados: List<ComponenteDieta>,
    onComponenteSeleccionado: (ComponenteDieta, Boolean) -> Unit
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
                                    val estaSeleccionado = componente in componentesSeleccionados
                                    onComponenteSeleccionado(componente, !estaSeleccionado)
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = componente in componentesSeleccionados,
                                onCheckedChange = { checked ->
                                    onComponenteSeleccionado(componente, checked)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = componente.nombre,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Calorías: ${componente.Kcal_ini}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
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
