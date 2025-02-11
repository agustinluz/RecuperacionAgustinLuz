package com.recuperacion.agustin.componentes

import ComponenteDieta
import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp



@Composable
fun EditarAlimentoDialog(
    alimento: ComponenteDieta,
    onGuardar: (ComponenteDieta) -> Unit,
    onBorrar: () -> Unit,
    onDismiss: () -> Unit
) {
    var nombre by remember { mutableStateOf(alimento.nombre) }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Editar Componente Dieta") },
        text = {
            LazyColumn { item {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text(text = "Nombre") }
                )
            }
            }

        },
        confirmButton = {
            Row {
                Button(
                    onClick = {
                        if (nombre.isEmpty()) {
                            Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                        } else {
                            onGuardar(alimento.copy(nombre = nombre))
                        }
                    }
                ) {
                    Text(text = "Guardar")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        onBorrar()
                    }
                ) {
                    Text(text = "Borrar")
                }
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = "Cancelar")
            }
        }
    )
}