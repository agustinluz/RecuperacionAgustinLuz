package com.recuperacion.agustin

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                    App(modifier = Modifier ,viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    modifier: Modifier = Modifier,
    viewModel: AlimentosMVVM = viewModel( factory = AlimentosMVVM.Factory(LocalContext.current.applicationContext as Application))
) {
    val navController = rememberNavController()
    var showMenu by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Menú",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.List, "Listado") },
                    label = { Text("Listado") },
                    selected = false,
                    onClick = {
                        navController.navigate("listado") {
                            popUpTo("listado") { inclusive = true }
                        }
                        showMenu = false
                    }
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Add, "Nuevo") },
                    label = { Text("Nuevo Componente") },
                    selected = false,
                    onClick = {
                        navController.navigate("formulario")
                        showMenu = false
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Alimentos") },
                    navigationIcon = {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "listado",
                modifier = modifier.padding(paddingValues)
            ) {
                composable("listado") {
                    ListadoDetalle(
                        viewModel = viewModel,
                        onNavigateToFormulario = { navController.navigate("formulario") },
                        onNavigateToEdit = { componente ->
                            viewModel.setComponenteToEdit(componente)
                            navController.navigate("formulario")
                        }
                    )
                }

                composable("formulario") {
                    Formulario(
                        viewModel = viewModel,
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}
