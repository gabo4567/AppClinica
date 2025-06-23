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
import com.clinica.app.network.EditarTurnoDTO


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

    var mostrarDialogoEditarTurno by remember { mutableStateOf(false) }
    var turnoParaEditar by remember { mutableStateOf<TurnoDTO?>(null) }

    var turnoParaActualizar by remember { mutableStateOf<TurnoDTO?>(null) }
    var mostrarDialogoAtendido by remember { mutableStateOf(false) }

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

    fun EditarTurnoDTO.toRegistroTurnoDTO(): RegistroTurnoDTO {
        return RegistroTurnoDTO(
            idPaciente = this.idPaciente,
            idProfesional = this.idProfesional,
            fechaHora = this.fechaHora,
            duracion = this.duracion,
            idEstado = this.idEstado,
            observaciones = this.observaciones
        )
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
                            modifier = Modifier.padding(start = 26.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Box(
                        modifier = Modifier.weight(1.5f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            "DNI",
                            modifier = Modifier.padding(start = 1.dp),
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
                            modifier = Modifier.offset(x = (-50).dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Box(
                        modifier = Modifier.weight(2.4f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            "Profesional",
                            modifier = Modifier.offset(x = (-115).dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Box(
                        modifier = Modifier.weight(1.5f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            "Fecha y Hora",
                            modifier = Modifier.offset(x = (-175).dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Box(
                        modifier = Modifier.weight(2f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            "Estado",
                            modifier = Modifier.offset(x = (-145).dp),
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
                                println("DEBUG - Turno seleccionado para editar ID: ${turnoAModificar.id}")
                                turnoParaEditar = turnoAModificar
                                mostrarDialogoEditarTurno = true
                            },
                            onMarcarComoAtendidoClick = { turnoAMarcar ->
                                turnoParaActualizar = turnoAMarcar
                                mostrarDialogoAtendido = true
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
                        val resultado = try {
                            TurnoApi.crearTurno(nuevoTurno)
                        } catch (e: Exception) {
                            val mensajeError = e.message ?: "Error desconocido"
                            if (mensajeError.contains("Turno superpuesto", ignoreCase = true)) {
                                snackbarHostState.showSnackbar("No se puede crear turno: ya hay uno reservado en ese horario.")
                            } else {
                                snackbarHostState.showSnackbar("Error al crear turno: $mensajeError")
                            }
                            return@launch
                        }

                        val exito = resultado.getOrElse { e ->
                            val mensajeError = e.message ?: "Error desconocido"
                            if (mensajeError.contains("Turno superpuesto")) {
                                snackbarHostState.showSnackbar("No se puede crear turno: ya hay uno reservado en ese horario.")
                            } else {
                                snackbarHostState.showSnackbar("Error al crear turno: $mensajeError")
                            }
                            false
                        }

                        if (exito) {
                            mostrarDialogoNuevoTurno = false
                            mensajeExito = "Turno creado correctamente"
                            cargarTurnos()
                        }
                        // En caso de error ya mostramos snackbar arriba, no es necesario else
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

        if (mostrarDialogoEditarTurno && turnoParaEditar != null) {
            val turnoNoNulo = turnoParaEditar!!  // ✅ Cast seguro porque ya verificaste que no es null
            val profesionalDelTurno = profesionales.find { it.idPersona == turnoNoNulo.idProfesional }

            if (profesionalDelTurno != null) {
                EditarTurnoForm(
                    turnoOriginal = turnoNoNulo,
                    profesionalDelTurno = profesionalDelTurno,
                    profesionales = profesionales,
                    onConfirmar = { turnoModificado: EditarTurnoDTO ->
                        println("DEBUG - ID turno modificado: ${turnoModificado.id}")
                        println("DEBUG - DTO sin ID (RegistroTurnoDTO): ${turnoModificado.toRegistroTurnoDTO()}")
                        scope.launch {
                            val resultado = try {
                                TurnoApi.actualizarTurno(
                                    turnoModificado.id,
                                    turnoModificado.toRegistroTurnoDTO()
                                )
                            } catch (e: Exception) {
                                val mensajeError = e.message ?: "Error desconocido"
                                println("DEBUG - Excepción al actualizar turno: $mensajeError")
                                snackbarHostState.showSnackbar("Error al actualizar turno: $mensajeError")
                                return@launch
                            }

                            val exito = resultado.getOrElse { e ->
                                val mensajeError = e.message ?: "Error desconocido"
                                println("DEBUG - Error al actualizar turno (getOrElse): $mensajeError")
                                if (mensajeError.contains("Turno superpuesto", ignoreCase = true)) {
                                    snackbarHostState.showSnackbar("No se puede modificar turno: ya hay uno reservado en ese horario.")
                                } else {
                                    snackbarHostState.showSnackbar("Error al actualizar turno: $mensajeError")
                                }
                                false
                            }

                            if (exito) {
                                mostrarDialogoEditarTurno = false
                                mensajeExito = "Turno actualizado correctamente"
                                cargarTurnos()
                            }
                        }
                    },
                    onCancelar = { mostrarDialogoEditarTurno = false },
                    obtenerFechasDisponiblesApi = { idProfesional ->
                        HorarioDisponibleApi.obtenerFechasDisponibles(idProfesional)
                    },
                    obtenerHorariosDisponiblesApi = { idProfesional, fecha ->
                        HorarioDisponibleApi.obtenerHorariosDisponibles(idProfesional, fecha)
                    }
                )
            } else {
                Text("No se pudo cargar el profesional del turno.")
            }
        }

        if (mostrarDialogoAtendido && turnoParaActualizar != null) {
            val turnoNoNulo = turnoParaActualizar!!  // ✅ Smart cast explícito

            AlertDialog(
                onDismissRequest = {
                    mostrarDialogoAtendido = false
                    turnoParaActualizar = null
                },
                title = { Text("Confirmar asistencia") },
                text = { Text("¿Deseas marcar este turno como atendido?") },
                confirmButton = {
                    TextButton(onClick = {
                        val turnoAtendido = turnoNoNulo.copy(idEstado = 13L)

                        scope.launch {
                            val resultado = try {
                                TurnoApi.actualizarTurno(
                                    turnoAtendido.id!!,
                                    RegistroTurnoDTO(
                                        idPaciente = turnoAtendido.idPaciente,
                                        idProfesional = turnoAtendido.idProfesional,
                                        fechaHora = turnoAtendido.fechaHora.toString(),
                                        duracion = turnoAtendido.duracion,
                                        idEstado = 13L,
                                        observaciones = turnoAtendido.observaciones
                                    )
                                )
                            } catch (e: Exception) {
                                val mensaje = e.message ?: "Error desconocido"
                                snackbarHostState.showSnackbar("Error al actualizar turno: $mensaje")
                                return@launch
                            }

                            val exito = resultado.getOrElse { e ->
                                val mensajeError = e.message ?: "Error desconocido"
                                println("DEBUG - Error al marcar como atendido: $mensajeError")
                                if (mensajeError.contains("Turno superpuesto", ignoreCase = true)) {
                                    snackbarHostState.showSnackbar("No se puede marcar como atendido: ya hay un turno en ese horario.")
                                } else {
                                    snackbarHostState.showSnackbar("Error al actualizar turno: $mensajeError")
                                }
                                false
                            }

                            if (exito) {
                                mensajeExito = "Turno marcado como atendido"
                                cargarTurnos()
                            }
                        }

                        mostrarDialogoAtendido = false
                        turnoParaActualizar = null
                    }) {
                        Text("Confirmar", color = Color(0xFF4CAF50))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        mostrarDialogoAtendido = false
                        turnoParaActualizar = null
                    }) {
                        Text("Cancelar", color = Color.Blue)
                    }
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
    onMarcarComoAtendidoClick: (TurnoDTO) -> Unit,
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
        Text(turno.comprobante, modifier = Modifier.weight(1.6f))
        Text(dniPaciente, modifier = Modifier.weight(1f))
        Text(nombrePaciente, modifier = Modifier.weight(1.2f))
        Text(telefonoPaciente, modifier = Modifier.weight(1.1f))
        Text(nombreProfesional, modifier = Modifier.weight(1.3f))
        Text(turno.fechaHora.toString(), modifier = Modifier.weight(1.2f))
        Text(
            nombreEstadoPorId(turno.idEstado),
            modifier = Modifier
                .weight(1f)
                .padding(start = 6.dp)
        )

        //  Botón "Atendido" pequeño y verde
        Button(
            onClick = { onMarcarComoAtendidoClick(turno) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)), // Verde
            modifier = Modifier
                .width(110.dp)
                .padding(end = 6.dp)
        ) {
            Text("Atendido", fontSize = 12.sp)
        }

        //  Botón "Modificar"
        Button(
            onClick = { onModificarClick(turno) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .width(110.dp)
                .padding(end = 6.dp)
        ) {
            Text("Modificar", fontSize = 12.sp)
        }

        //  Botón "Cancelar"
        Button(
            onClick = { onCancelarClick(turno) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.width(100.dp)
        ) {
            Text("Cancelar", fontSize = 12.sp)
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
