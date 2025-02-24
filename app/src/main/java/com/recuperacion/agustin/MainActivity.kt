package com.recuperacion.agustin

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.*
import com.recuperacion.agustin.screens.Formulario
import com.recuperacion.agustin.componentes.MiTopAppBar
import com.recuperacion.agustin.modelo.AlimentosMVVM
import com.recuperacion.agustin.modelo.AlimentosMVVMFactory
import com.recuperacion.agustin.room.AppDatabase
import com.recuperacion.agustin.modelo.AlimentosRepository
import com.recuperacion.agustin.screens.Ruta
import com.recuperacion.agustin.modelo.IngredienteRepository
import com.recuperacion.agustin.screens.ListadoDetalle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val database = AppDatabase.getDatabase(context)
    
    val alimentosRepository = AlimentosRepository(database.componenteDietaDao())
    val ingredienteRepository = IngredienteRepository(database.ingredienteDao())
    
    val viewModel: AlimentosMVVM = viewModel(
        factory = AlimentosMVVMFactory(
            application = application,
            alimentosRepository = alimentosRepository,
            ingredienteRepository = ingredienteRepository
        )
    )

    Scaffold(
        topBar = {
            MiTopAppBar(
                onNavigateToFormulario = { navController.navigate(Ruta.Formulario.ruta) },
                onNavigateToListado = { navController.navigate(Ruta.ListadoDetalle.ruta) }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Ruta.Formulario.ruta,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Ruta.Formulario.ruta) {
                Formulario(viewModel)
            }
            composable(
                route = "${Ruta.Formulario.ruta}/{componenteId}",
                arguments = listOf(navArgument("componenteId") { type = NavType.IntType })
            ) { backStackEntry ->
                val componenteId = backStackEntry.arguments?.getInt("componenteId") ?: return@composable
                val componente = viewModel.obtenerComponentePorId(componenteId)
                Formulario(viewModel, componente)
            }
            composable(Ruta.ListadoDetalle.ruta) {
                ListadoDetalle(
                    viewModel = viewModel,
                    onNavigateToEdit = { componente ->
                        navController.navigate("${Ruta.Formulario.ruta}/${componente.id}")
                    }
                )
            }
        }
    }
}
