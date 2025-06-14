package com.clinica.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch

enum class MenuOption(val label: String) {
    DASHBOARD("Inicio"),
    PACIENTES("Pacientes"),
    TURNOS("Turnos"),
    PROFESIONALES("Profesionales"),
    ESPECIALIDADES("Especialidades"),
    CONSULTAS("Consultas"),
    LOGOUT("Cerrar sesión")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onLogout: () -> Unit) {
    var selectedOption by remember { mutableStateOf(MenuOption.DASHBOARD) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    MenuOption.values().forEach { option ->
                        Text(
                            text = option.label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    if (option == MenuOption.LOGOUT) {
                                        onLogout()
                                    } else {
                                        selectedOption = option
                                        scope.launch { drawerState.close() }
                                    }
                                },
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (selectedOption == option) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Clínica Salud Total - Secretaría") },
                    navigationIcon = {
                        Text(
                            text = "☰", // ícono textual estilo hamburguesa
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .clickable {
                                    scope.launch {
                                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                    }
                                },
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                when (selectedOption) {
                    MenuOption.DASHBOARD -> InicioScreen()
                    MenuOption.PACIENTES -> PacientesScreen()
                    MenuOption.TURNOS -> TurnosScreen()
                    MenuOption.PROFESIONALES -> Text("Gestión de Profesionales")
                    MenuOption.ESPECIALIDADES -> EspecialidadScreen()
                    MenuOption.CONSULTAS -> Text("Pantalla de Consultas")
                    else -> {}
                }
            }
        }
    }
}
