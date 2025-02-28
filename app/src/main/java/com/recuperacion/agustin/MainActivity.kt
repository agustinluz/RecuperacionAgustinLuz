package com.recuperacion.agustin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.recuperacion.agustin.screens.Formulario
import com.recuperacion.agustin.screens.ListadoDetalle
import com.recuperacion.agustin.ui.theme.RecuperacionAgustinLuzTheme
import com.recuperacion.agustin.viewmodel.AlimentosMVVM

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: AlimentosMVVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, AlimentosMVVM.Factory(application))[AlimentosMVVM::class.java]

        setContent {
            RecuperacionAgustinLuzTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainContent(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(viewModel: AlimentosMVVM) {
    // Cambio aquí: ahora inicia en FORMULARIO en vez de LISTADO
    val currentScreenState = remember { mutableStateOf(Screen.FORMULARIO) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Gestión de Alimentos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        when (currentScreenState.value) {
            Screen.LISTADO -> ListadoDetalle(
                viewModel = viewModel,
                onNavigateToFormulario = { currentScreenState.value = Screen.FORMULARIO },
                modifier = Modifier.padding(paddingValues)
            )
            Screen.FORMULARIO -> Formulario(
                viewModel = viewModel,
                onNavigateBack = { currentScreenState.value = Screen.LISTADO }
            )
        }
    }
}

enum class Screen {
    LISTADO,
    FORMULARIO
}
