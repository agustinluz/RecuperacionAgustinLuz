// MainActivity.kt
package com.recuperacion.agustin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.recuperacion.agustin.screens.Formulario
import com.recuperacion.agustin.screens.ListadoDetalle
import com.recuperacion.agustin.componentes.MiTopAppBar
import com.recuperacion.agustin.modelo.AlimentosMVVM
import com.recuperacion.agustin.screens.Ruta

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navigationController = rememberNavController()
    val viewModel: AlimentosMVVM = viewModel()

    Scaffold(topBar = {
        MiTopAppBar(
            onNavigateToFormulario = { navigationController.navigate(Ruta.Formulario.ruta) },
            onNavigateToListado = { navigationController.navigate(Ruta.ListadoDetalle.ruta) }
        )
    }) { paddingValues ->
        NavHost(
            navController = navigationController,
            startDestination = Ruta.Formulario.ruta,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Ruta.Formulario.ruta) {
                Formulario(viewModel)
            }
            composable(Ruta.ListadoDetalle.ruta) {
                ListadoDetalle(viewModel)
            }
        }
    }
}

