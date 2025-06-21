package com.clinica.app.ui

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
import ProfesionalDTO
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.unit.sp
import com.clinica.app.network.*
import kotlinx.coroutines.delay

fun nombreEspecialidadPorId(idEspecialidad: Long?): String {
    return when (idEspecialidad) {
        1L -> "Clínica General"
        2L -> "Pediatría"
        3L -> "Cardiología"
        4L -> "Ginecología"
        else -> "Desconocida"
    }
}

@Composable
fun TurnosScreen() {
    val scope = rememberCoroutineScope()
    var turnos by remember { mutableStateOf<List<TurnoDTO>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    var profesionales by remember { mutableStateOf<List<ProfesionalDTO>>(emptyList()) }
    var pacientes by remember { mutableStateOf<List<Paciente>>(emptyList()) }

    var turnoParaCancelar by remember { mutableStateOf<TurnoDTO?>(null) }
    var mostrarDialogoConfirmacion by remember { mutableStateOf(false) }

    var mostrarDialogoNuevoTurno by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    var mensajeExito by remember { mutableStateOf<String?>(null) }

    var filtroDniPaciente by remember { mutableStateOf("") }
    var filtroFecha by remember { mutableStateOf<String?>(null) }
    var filtroProfesional by remember { mutableStateOf("") }
    var filtroPaciente by remember { mutableStateOf("") }
    val filtroEstado = remember { mutableStateOf<String?>(null) }

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

    fun dniPacientePorId(idPersona: Long?): String {
        return pacientes.find { it.persona.id == idPersona }?.persona?.dni ?: "-"
    }

    fun telefonoPacientePorId(idPersona: Long?): String {
        return pacientes.find { it.persona.id == idPersona }?.persona?.telefono ?: "-"
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
                mensajeExito = "Turno cancelado correctamente"  // <-- aquí cambio importante
            } else {
                snackbarHostState.showSnackbar("Error al cancelar turno")
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.fillMaxWidth(),
                    snackbarData = data
                )
            }
        }

    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 1.dp)
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(vertical = 15.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Lista de Turnos - Utilización de Filtros para Informes",
                    style = MaterialTheme.typography.headlineSmall,
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.Start),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = filtroDniPaciente,
                    onValueChange = { filtroDniPaciente = it },
                    label = { Text("Filtrar Paciente (DNI)") },
                    modifier = Modifier.width(200.dp),
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = filtroPaciente,
                    onValueChange = { filtroPaciente = it },
                    label = { Text("Filtrar Paciente (nombre o apellido)") },
                    modifier = Modifier.width(300.dp),
                    shape = RoundedCornerShape(16.dp)
                )

                OutlinedTextField(
                    value = filtroProfesional,
                    onValueChange = { filtroProfesional = it },
                    label = { Text("Filtrar Profesional (nombre o apellido)") },
                    modifier = Modifier.width(325.dp),
                    shape = RoundedCornerShape(16.dp)
                )

                DropdownFiltro(
                    label = "Estado",
                    opciones = estados,
                    seleccion = filtroEstado.value ?: "Todos",
                    onSeleccion = { seleccion ->
                        filtroEstado.value = if (seleccion == "Todos") null else seleccion
                    },
                    modifier = Modifier.width(150.dp),
                )

                OutlinedTextField(
                    value = filtroFecha ?: "",
                    onValueChange = { filtroFecha = it },
                    label = { Text("Fecha (aaaa-mm-dd)") },
                    modifier = Modifier.width(200.dp),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.width(45.dp))

                Button(
                    onClick = { mostrarDialogoNuevoTurno = true },
                    modifier = Modifier.height(45.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF66BB6A),
                        contentColor = Color.White
                    )
                ) {
                    Text("Nuevo Turno", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

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

                    val dniPac = dniPacientePorId(turno.idPaciente)
                    val cumpleDni = filtroDniPaciente.isBlank() || dniPac.contains(filtroDniPaciente, ignoreCase = true)

                    val nombrePac = nombrePacientePorId(turno.idPaciente)
                    val cumplePaciente = filtroPaciente.isBlank() ||
                            nombrePac.contains(filtroPaciente, ignoreCase = true)

                    val cumpleEstado = filtroEstado.value == null || filtroEstado.value.equals(nombreEstado, ignoreCase = true)

                    cumpleFecha && cumpleProfesional && cumplePaciente && cumpleEstado && cumpleDni
                }

                // Encabezado tabla
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(vertical = 8.dp, horizontal = 12.dp)
                ) {
                    Box(
                        modifier = Modifier.weight(2.5f),
                        contentAlignment = Alignment.CenterStart // texto alineado a la izquierda
                    ) {
                        Text(
                            "Comprobante",
                            modifier = Modifier.padding(start = 15.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Box(
                        modifier = Modifier.weight(1.5f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            "DNI",
                            modifier = Modifier.padding(start = 5.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Box(
                        modifier = Modifier.weight(2f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            "Paciente",
                            modifier = Modifier.offset(x = (-20).dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Box(
                        modifier = Modifier.weight(2.4f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            "Teléfono",
                            modifier = Modifier.offset(x = (-40).dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Box(
                        modifier = Modifier.weight(2.4f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            "Profesional",
                            modifier = Modifier.offset(x = (-95).dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Box(
                        modifier = Modifier.weight(1.5f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            "Fecha y Hora",
                            modifier = Modifier.offset(x = (-115).dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Box(
                        modifier = Modifier.weight(2f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            "Estado",
                            modifier = Modifier.offset(x = (-70).dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Spacer(modifier = Modifier.width(100.dp)) // Para el botón cancelar
                }

                // Divider()

                LazyColumn {
                    items(turnosFiltrados) { turno ->
                        val nombrePaciente = nombrePacientePorId(turno.idPaciente)
                        val nombreProfesional = nombreProfesionalPorId(turno.idProfesional)
                        val dniPaciente = dniPacientePorId(turno.idPaciente)
                        val telefonoPaciente = telefonoPacientePorId(turno.idPaciente)

                        TurnoRowItem(
                            turno = turno,
                            onCancelarClick = { turnoACancelar ->
                                turnoParaCancelar = turnoACancelar
                                mostrarDialogoConfirmacion = true
                            },
                            onModificarClick = { turnoAModificar ->
                                println("Modificar turno: ${turnoAModificar.comprobante}")
                                // Aquí podés abrir un diálogo o navegar a pantalla de edición
                            },
                            nombrePaciente = nombrePaciente,
                            nombreProfesional = nombreProfesional,
                            dniPaciente = dniPaciente,
                            telefonoPaciente = telefonoPaciente
                        )
                        Divider()
                    }
                }

            }
        }
        if (mostrarDialogoConfirmacion && turnoParaCancelar != null) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoConfirmacion = false },
                title = { Text("Confirmar cancelación") },
                text = { Text("¿Estás seguro de que deseas cancelar este turno?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            cancelarTurno(turnoParaCancelar!!)
                            mostrarDialogoConfirmacion = false
                        }
                    ) {
                        Text("Confirmar", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { mostrarDialogoConfirmacion = false }
                    ) {
                        Text("Cancelar", color = Color.Blue)
                    }
                }
            )
        }

        if (mostrarDialogoNuevoTurno) {
            AgregarTurnoForm(
                pacientes = pacientes,
                profesionales = profesionales,
                onConfirmar = { nuevoTurno ->
                    scope.launch {
                        val exito = try {
                            TurnoApi.crearTurno(nuevoTurno)
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar("Error al crear turno: ${e.message}")
                            false
                        }

                        if (exito) {
                            mostrarDialogoNuevoTurno = false
                            mensajeExito = "Turno creado correctamente"
                            cargarTurnos()
                        } else {
                            snackbarHostState.showSnackbar("Error al crear turno")
                        }
                    }
                },
                onCancelar = { mostrarDialogoNuevoTurno = false },
                obtenerFechasDisponiblesApi = { idProfesional ->
                    HorarioDisponibleApi.obtenerFechasDisponibles(idProfesional)
                },
                obtenerHorariosDisponiblesApi = { idProfesional, fecha ->
                    HorarioDisponibleApi.obtenerHorariosDisponibles(idProfesional, fecha)
                }
            )
        }


    }

    mensajeExito?.let { mensaje ->
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(durationMillis = 600)) + slideInVertically(initialOffsetY = { -40 }),
            exit = fadeOut(animationSpec = tween(durationMillis = 600)) + slideOutVertically(targetOffsetY = { -40 })
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Snackbar {
                    Text(mensaje)
                }
            }
        }

        // Ocultar automáticamente después de 3 segundos
        LaunchedEffect(mensaje) {
            delay(3000)
            mensajeExito = null
        }
    }

}

@Composable
fun TurnoRowItem(
    turno: TurnoDTO,
    onCancelarClick: (TurnoDTO) -> Unit,
    onModificarClick: (TurnoDTO) -> Unit,  // NUEVO parámetro para el botón Modificar
    nombrePaciente: String,
    nombreProfesional: String,
    dniPaciente: String,
    telefonoPaciente: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(turno.comprobante, modifier = Modifier.weight(2f))
        Text(dniPaciente, modifier = Modifier.weight(1.2f))
        Text(nombrePaciente, modifier = Modifier.weight(1.5f))
        Text(telefonoPaciente, modifier = Modifier.weight(1.5f))
        Text(nombreProfesional, modifier = Modifier.weight(1.8f))
        Text(turno.fechaHora.toString(), modifier = Modifier.weight(1.5f))
        Text(nombreEstadoPorId(turno.idEstado), modifier = Modifier.weight(1.5f).padding(start = 20.dp))

        // Botón Modificar agregado justo antes de Cancelar
        Button(
            onClick = { onModificarClick(turno) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .width(120.dp)
                .padding(end = 8.dp)
        ) {
            Text("Modificar")
        }

        Button(
            onClick = { onCancelarClick(turno) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.width(105.dp)
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
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
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
