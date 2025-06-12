package com.clinica.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.clinica.app.models.TurnoDTO
import com.clinica.app.models.Paciente
import com.clinica.app.network.PacienteService
import com.clinica.app.network.ProfesionalService
import com.clinica.app.network.TurnoApi
import ProfesionalDTO
import com.clinica.app.network.nombreEstadoPorId
import kotlinx.coroutines.launch

@Composable
fun TurnosScreen() {
    val scope = rememberCoroutineScope()
    var turnos by remember { mutableStateOf<List<TurnoDTO>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    var profesionales by remember { mutableStateOf<List<ProfesionalDTO>>(emptyList()) }
    var pacientes by remember { mutableStateOf<List<Paciente>>(emptyList()) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Filtros seleccionados ahora son textos libres para profesional y paciente
    var filtroFecha by remember { mutableStateOf<String?>(null) }
    var filtroProfesional by remember { mutableStateOf("") }
    var filtroPaciente by remember { mutableStateOf("") }
    val filtroEstado = remember { mutableStateOf<String?>(null) }


    // Estados para filtro de estado
    val estados = listOf("Todos") + listOf(
        nombreEstadoPorId(9L),
        nombreEstadoPorId(10L),
        nombreEstadoPorId(11L),
        nombreEstadoPorId(12L),
        nombreEstadoPorId(13L)
    )

    suspend fun cargarTurnos() {
        cargando = true
        try {
            turnos = TurnoApi.obtenerTodosLosTurnos()
        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar("Error al cargar turnos: ${e.message}")
            }
        } finally {
            cargando = false
        }
    }

    suspend fun cargarProfesionales() {
        try {
            profesionales = ProfesionalService.getProfesionales()
        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar("Error al cargar profesionales: ${e.message}")
            }
        }
    }

    suspend fun cargarPacientes() {
        try {
            pacientes = PacienteService.getPacientes()
        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar("Error al cargar pacientes: ${e.message}")
            }
        }
    }

    LaunchedEffect(Unit) {
        cargarProfesionales()
        cargarPacientes()
        cargarTurnos()
    }

    fun nombreProfesionalPorId(idPersona: Long?): String {
        return profesionales.find { it.idPersona == idPersona }
            ?.let { "${it.nombre} ${it.apellido}" } ?: "Desconocido"
    }

    fun nombrePacientePorId(idPersona: Long?): String {
        return pacientes.find { it.persona.id == idPersona }
            ?.let { "${it.persona.nombre} ${it.persona.apellido}" } ?: "Desconocido"
    }

    fun cancelarTurno(turno: TurnoDTO) {
        val id = turno.id
        if (id == null) {
            scope.launch {
                snackbarHostState.showSnackbar("ID de turno inválido")
            }
            return
        }

        val turnoCancelado = turno.copy(idEstado = 11)

        scope.launch {
            val exito = try {
                TurnoApi.cancelarTurno(turnoCancelado)
            } catch (e: Exception) {
                false
            }
            if (exito) {
                turnos = turnos.map {
                    if (it.id == turnoCancelado.id) turnoCancelado else it
                }
                snackbarHostState.showSnackbar("Turno cancelado correctamente")
            } else {
                snackbarHostState.showSnackbar("Error al cancelar turno")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding)
        ) {
            Text("Lista de Turnos", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            // Filtros de Profesional y Paciente como textos libres con búsqueda dinámica
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = filtroProfesional,
                    onValueChange = { filtroProfesional = it },
                    label = { Text("Filtrar Profesional (nombre o apellido)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = filtroPaciente,
                    onValueChange = { filtroPaciente = it },
                    label = { Text("Filtrar Paciente (nombre o apellido)") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Filtro de Estado como Dropdown (select)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DropdownFiltro(
                    label = "Estado",
                    opciones = estados,
                    seleccion = filtroEstado.value ?: "Todos",
                    onSeleccion = { seleccion ->
                        filtroEstado.value = if (seleccion == "Todos") null else seleccion
                    },
                    modifier = Modifier.weight(1f)
                )



                OutlinedTextField(
                    value = filtroFecha ?: "",
                    onValueChange = { filtroFecha = it },
                    label = { Text("Fecha") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (cargando) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val turnosFiltrados = turnos.filter { turno ->
                    val nombreEstado = nombreEstadoPorId(turno.idEstado)

                    val cumpleFecha = filtroFecha.isNullOrBlank() || turno.fechaHora.toString()
                        .contains(filtroFecha ?: "", ignoreCase = true)

                    val nombreProf = nombreProfesionalPorId(turno.idProfesional)
                    val cumpleProfesional = filtroProfesional.isBlank() ||
                            nombreProf.contains(filtroProfesional, ignoreCase = true)

                    val nombrePac = nombrePacientePorId(turno.idPaciente)
                    val cumplePaciente = filtroPaciente.isBlank() ||
                            nombrePac.contains(filtroPaciente, ignoreCase = true)

                    val cumpleEstado = filtroEstado.value == null || filtroEstado.value.equals(nombreEstado, ignoreCase = true)

                    cumpleFecha && cumpleProfesional && cumplePaciente && cumpleEstado
                }


                LazyColumn {
                    items(turnosFiltrados) { turno ->
                        val nombrePaciente = nombrePacientePorId(turno.idPaciente)
                        val nombreProfesional = nombreProfesionalPorId(turno.idProfesional)

                        TurnoItem(
                            turno = turno,
                            onCancelarClick = { turnoACancelar -> cancelarTurno(turnoACancelar) },
                            nombrePaciente = nombrePaciente,
                            nombreProfesional = nombreProfesional
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun TurnoItem(
    turno: TurnoDTO,
    onCancelarClick: (TurnoDTO) -> Unit,
    nombrePaciente: String,
    nombreProfesional: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Comprobante: ${turno.comprobante}")
            Text("Paciente: $nombrePaciente | Profesional: $nombreProfesional")
            Text("Fecha: ${turno.fechaHora} | Estado: ${nombreEstadoPorId(turno.idEstado)}")
        }
        Button(
            onClick = { onCancelarClick(turno) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text("Cancelar")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownFiltro(
    label: String,
    opciones: List<String>,
    seleccion: String,
    onSeleccion: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = seleccion,
                onValueChange = {},
                label = { Text(label) },
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            println("Opción seleccionada: $opcion") // Para depurar
                            onSeleccion(opcion)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
