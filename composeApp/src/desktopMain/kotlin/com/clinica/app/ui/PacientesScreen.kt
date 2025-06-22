package com.clinica.app.ui

import RegistroPacienteDTO
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clinica.app.models.Paciente
import com.clinica.app.network.AgregarPacienteForm
import com.clinica.app.network.PacienteService
import com.clinica.app.network.EditarPacienteForm
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PacientesScreen() {
    var pacientes by remember { mutableStateOf<List<Paciente>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var estadoFiltro by remember { mutableStateOf<Long?>(null) } // null = todos, 1L = activo, 2L = inactivo
    var filtroDniPaciente by remember { mutableStateOf("") } // NUEVO: filtro por DNI

    var mostrarFormularioAgregar by remember { mutableStateOf(false) }
    var isProcessingAgregar by remember { mutableStateOf(false) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }
    var agregarErrorMessage by remember { mutableStateOf<String?>(null) }

    var pacienteAEditar by remember { mutableStateOf<Paciente?>(null) }
    var editErrorMessage by remember { mutableStateOf<String?>(null) }
    var isProcessingEdit by remember { mutableStateOf(false) }

    var pacienteAEliminar by remember { mutableStateOf<Paciente?>(null) }
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var eliminarErrorMessage by remember { mutableStateOf<String?>(null) }
    var isProcessingEliminar by remember { mutableStateOf(false) }


    // val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Cargar pacientes inicial y refrescar
    suspend fun cargarPacientes() {
        isLoading = true
        errorMessage = null
        try {
            pacientes = PacienteService.getPacientes()
        } catch (e: Exception) {
            errorMessage = "Error al cargar pacientes: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    // Carga inicial
    LaunchedEffect(Unit) {
        cargarPacientes()
    }

    // Filtrar pacientes según estadoFiltro y filtroDniPaciente
    val pacientesFiltrados = pacientes.filter { paciente ->
        val cumpleEstado = estadoFiltro == null || paciente.idEstado == estadoFiltro
        val cumpleDni = filtroDniPaciente.isBlank() || paciente.persona.dni.contains(filtroDniPaciente, ignoreCase = true)
        cumpleEstado && cumpleDni
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 0.dp)
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)), // bordes redondeados
                color = Color(0xFF1976D2), // azul
                shadowElevation = 4.dp,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 24.dp), // vertical reducido a 12.dp
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Gestión de Pacientes de la Clínica",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filtro por estado + botón Agregar alineado a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Filtrar pacientes por estado: ",
                    modifier = Modifier.padding(end = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Button(
                    onClick = { estadoFiltro = null },
                    colors = if (estadoFiltro == null)
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2),  // azul fuerte (activo)
                            contentColor = Color.Black
                        )
                    else
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFBBDEFB),  // azul clarito (inactivo)
                            contentColor = Color.Black
                        ),
                    border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.3f))
                ) {
                    Text("Todos", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { estadoFiltro = 1L },
                    colors = if (estadoFiltro == 1L)
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2),
                            contentColor = Color.Black
                        )
                    else
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFBBDEFB),
                            contentColor = Color.Black
                        ),
                    border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.3f))
                ) {
                    Text("Activos", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { estadoFiltro = 2L },
                    colors = if (estadoFiltro == 2L)
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2),
                            contentColor = Color.Black
                        )
                    else
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFBBDEFB),
                            contentColor = Color.Black
                        ),
                    border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.3f))
                ) {
                    Text("Inactivos", fontSize = 16.sp)
                }

                OutlinedTextField(
                    value = filtroDniPaciente,
                    onValueChange = { filtroDniPaciente = it },
                    label = { Text("Filtrar Paciente (DNI)") },
                    modifier = Modifier
                        .width(240.dp)
                        .padding(top = 1.dp, start = 15.dp), // Ajusta estos valores a tu gusto
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.weight(0.1f))

                Button(
                    onClick = { mostrarFormularioAgregar = true },
                    modifier = Modifier.padding(end = 40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF66BB6A),
                        contentColor = Color.White
                    )
                ) {
                    Text("Agregar Paciente", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            } else {
                PacientesHeader()
                Spacer(modifier = Modifier.height(1.dp))

                if (mostrarDialogoEliminar && pacienteAEliminar != null) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialogoEliminar = false },
                        title = { Text("Confirmar eliminación") },
                        text = { Text("¿Estás seguro de que deseas eliminar al paciente?") },
                        confirmButton = {
                            TextButton(onClick = {
                                eliminarErrorMessage = null
                                isProcessingEliminar = true
                                mostrarDialogoEliminar = false
                                coroutineScope.launch {
                                    try {
                                        PacienteService.eliminarPaciente(pacienteAEliminar!!.id)
                                        cargarPacientes()
                                        // 🎉 Mostrar mensaje de éxito
                                        mensajeExito = "Paciente eliminado con éxito"

                                    } catch (e: Exception) {
                                        eliminarErrorMessage = "Error al eliminar paciente: ${e.message}"
                                        e.printStackTrace()
                                    } finally {
                                        isProcessingEliminar = false
                                    }
                                }
                            }) {
                                Text("Confirmar", color = Color.Red)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                mostrarDialogoEliminar = false
                                pacienteAEliminar = null
                            }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }

                LazyColumn {
                    items(pacientesFiltrados) { paciente ->
                        PacienteItem(
                            paciente = paciente,
                            onEditarClick = {
                                pacienteAEditar = it
                                editErrorMessage = null
                            },
                            onEliminarClick = {
                                pacienteAEliminar = it
                                mostrarDialogoEliminar = true
                            }
                        )
                    }
                }
            }
        }

        // Mostrar mensaje centrado si se agregó paciente
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
                    Snackbar(
                        // containerColor = Color(0xFF4CAF50) // verde éxito
                    ) {
                        Text(mensaje//, color = Color.White
                        )
                    }
                }
            }

            // Ocultarlo automáticamente después de 3 segundos
            LaunchedEffect(mensaje) {
                delay(3000)
                mensajeExito = null
            }
        }

    }

    if (mostrarFormularioAgregar) {
        AlertDialog(
            onDismissRequest = { if (!isProcessingAgregar) mostrarFormularioAgregar = false },
            title = { Text("Agregar Nuevo Paciente") },
            text = {
                if (isProcessingAgregar) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column {
                        AgregarPacienteForm(
                            onConfirmar = { dto ->
                                mostrarFormularioAgregar = false  // Cerrar diálogo inmediatamente
                                isProcessingAgregar = true
                                agregarErrorMessage = null
                                coroutineScope.launch {
                                    try {
                                        PacienteService.crearPaciente(dto)
                                        cargarPacientes()
                                        mostrarFormularioAgregar = false

                                        // 🎉 Mostrar mensaje de éxito
                                        mensajeExito = "Paciente agregado con éxito"
                                    } catch (e: Exception) {
                                        agregarErrorMessage = "Error al agregar paciente: ${e.message}"
                                    } finally {
                                        isProcessingAgregar = false
                                    }
                                }
                            },
                            onCancelar = { mostrarFormularioAgregar = false }
                        )
                        if (agregarErrorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                agregarErrorMessage!!,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            },
            confirmButton = {},  // Vacíos porque botones están dentro del formulario
            dismissButton = {}
        )
    }


    // DIALOGO DE EDICIÓN
    if (pacienteAEditar != null) {
        AlertDialog(
            onDismissRequest = { if (!isProcessingEdit) pacienteAEditar = null },
            title = { Text("Modificar Paciente") },
            text = {
                if (isProcessingEdit) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    EditarPacienteForm(
                        paciente = pacienteAEditar!!,
                        onConfirmar = { pacienteActualizado ->
                            isProcessingEdit = true
                            editErrorMessage = null
                            coroutineScope.launch {
                                try {
                                    // Mapeo paciente a DTO para API (sin idRol ni idEspecialidad cambiables)
                                    val dto = pacienteActualizado.toRegistroPacienteDTO()
                                    PacienteService.actualizarPaciente(pacienteActualizado.id, dto)
                                    // Refrescar lista tras editar
                                    cargarPacientes()
                                    pacienteAEditar = null
                                    // 🎉 Mostrar mensaje de éxito
                                    mensajeExito = "Paciente modificado con éxito"
                                } catch (e: Exception) {
                                    editErrorMessage = "Error al actualizar paciente: ${e.message}"
                                    e.printStackTrace()
                                } finally {
                                    isProcessingEdit = false
                                }
                            }
                        },
                        onCancelar = { pacienteAEditar = null }
                    )
                    if (editErrorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(editErrorMessage!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

    // Snackbar para error eliminar
    eliminarErrorMessage?.let {
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { eliminarErrorMessage = null }) {
                    Text("Cerrar")
                }
            }
        ) { Text(it) }
    }
}

@Composable
fun PacientesHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "DNI",
                modifier = Modifier
                    .padding(start = 25.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                "Nombre",
                modifier = Modifier
                    .padding(start = 60.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
            Text(
                "Apellido",
                modifier = Modifier
                    .padding(start = 55.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
            Text(
                "Email",
                modifier = Modifier
                    .padding(start = 125.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
            Text(
                "Teléfono",
                modifier = Modifier
                    .padding(start = 180.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                "Dirección",
                modifier = Modifier
                    .padding(start = 130.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
            Text(
                "Fecha Nacimiento",
                modifier = Modifier
                    .padding(start = 110.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(0.dp)) // espacio para botones
        }
    }
}

@Composable
fun PacienteItem(
    paciente: Paciente,
    onEditarClick: (Paciente) -> Unit = {},
    onEliminarClick: (Paciente) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(paciente.persona.dni, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(paciente.persona.nombre, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(paciente.persona.apellido, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(paciente.persona.email, modifier = Modifier.weight(2.5f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(paciente.persona.telefono, modifier = Modifier.weight(1.5f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(paciente.persona.direccion, modifier = Modifier.weight(2f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(paciente.persona.fechaNacimiento, modifier = Modifier.weight(1.5f), maxLines = 1, overflow = TextOverflow.Ellipsis)

            Button(
                onClick = { onEditarClick(paciente) },
                modifier = Modifier
                    .width(120.dp)
                    .padding(end = 4.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text("Modificar", fontSize = 14.sp)
            }

            Button(
                onClick = { onEliminarClick(paciente) },
                modifier = Modifier
                    .width(105.dp)
                    .padding(start = 4.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar", fontSize = 14.sp)
            }

        }
    }
}

/**
 * Mapear Paciente a RegistroPacienteDTO para la actualización.
 * IMPORTANTE: Aquí se eliminan idRol e idEspecialidad para que
 * no se modifiquen desde el formulario (usar valores constantes o los actuales si los necesitas).
 */
fun Paciente.toRegistroPacienteDTO(): RegistroPacienteDTO {
    return RegistroPacienteDTO(
        dni = persona.dni,
        nombre = persona.nombre,
        apellido = persona.apellido,
        email = persona.email,
        telefono = persona.telefono,
        direccion = persona.direccion,
        fechaNacimiento = persona.fechaNacimiento, // debe estar en "yyyy-MM-dd"
        obraSocial = obraSocial,
        idRol = 4,              // Valor fijo, porque no se debe modificar aquí
        idEspecialidad = null,  // Siempre null en paciente
        idEstadoPersona = 1,    // Si es fijo, o usar paciente.persona.idEstadoPersona si quieres conservar el estado real
        idEstadoPaciente = 1    // Este valor se envía al actualizar. Si quieres, podrías mantener el estado actual del paciente en vez de forzar 1
    )
}
