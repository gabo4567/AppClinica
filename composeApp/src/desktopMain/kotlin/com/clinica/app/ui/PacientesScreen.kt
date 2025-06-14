package com.clinica.app.ui

import RegistroPacienteDTO
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.clinica.app.models.Paciente
import com.clinica.app.network.PacienteService
import com.clinica.app.network.EditarPacienteForm
import kotlinx.coroutines.launch

@Composable
fun PacientesScreen() {
    var pacientes by remember { mutableStateOf<List<Paciente>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var estadoFiltro by remember { mutableStateOf<Long?>(null) } // null = todos, 1L = activo, 2L = inactivo

    var pacienteAEditar by remember { mutableStateOf<Paciente?>(null) }
    var editErrorMessage by remember { mutableStateOf<String?>(null) }
    var isProcessingEdit by remember { mutableStateOf(false) }

    var eliminarErrorMessage by remember { mutableStateOf<String?>(null) }
    var isProcessingEliminar by remember { mutableStateOf(false) }

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

    // Filtrar pacientes según estadoFiltro
    val pacientesFiltrados = pacientes.filter { paciente ->
        estadoFiltro == null || paciente.idEstado == estadoFiltro
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Pacientes", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // FILTRO POR ESTADO + Botón Agregar alineado a la derecha
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Filtrar paciente por estado: ", modifier = Modifier.padding(end = 8.dp))

            Button(
                onClick = { estadoFiltro = null },
                colors = if (estadoFiltro == null) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) else ButtonDefaults.buttonColors()
            ) {
                Text("Todos")
            }
            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { estadoFiltro = 1L },
                colors = if (estadoFiltro == 1L) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) else ButtonDefaults.buttonColors()
            ) {
                Text("Activos")
            }
            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = { estadoFiltro = 2L },
                colors = if (estadoFiltro == 2L) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) else ButtonDefaults.buttonColors()
            ) {
                Text("Inactivos")
            }

            // Espaciador que empuja el botón a la derecha
            Spacer(modifier = Modifier.weight(1f))

            // Botón Agregar Paciente (por ahora sin funcionalidad)
            Button(onClick = { /* TODO: Abrir formulario en el futuro */ }) {
                Text("Agregar Paciente")
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage != null) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
        } else {
            PacientesHeader()
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(pacientesFiltrados) { paciente ->
                    PacienteItem(
                        paciente = paciente,
                        onEditarClick = {
                            pacienteAEditar = it
                            editErrorMessage = null
                        },
                        onEliminarClick = {
                            eliminarErrorMessage = null
                            isProcessingEliminar = true
                            coroutineScope.launch {
                                try {
                                    // Aquí en vez de eliminar físicamente, cambiar estado a inactivo (idEstadoPaciente=2)
                                    PacienteService.eliminarPaciente(it.id)
                                    // Refrescar lista luego de "eliminar"
                                    cargarPacientes()
                                } catch (e: Exception) {
                                    eliminarErrorMessage = "Error al eliminar paciente: ${e.message}"
                                    e.printStackTrace()
                                } finally {
                                    isProcessingEliminar = false
                                }
                            }
                        }
                    )
                }
            }
        }
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("DNI", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
            Text("Nombre", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
            Text("Apellido", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)

            // Para que EMAIL vaya más a la derecha, aumentar peso (puse 3f en vez de 2.5f)
            Text("Email", modifier = Modifier.weight(3f), style = MaterialTheme.typography.labelMedium)

            Text("Teléfono", modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelMedium)
            Text("Dirección", modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelMedium)
            Text("Fecha Nac.", modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.width(160.dp)) // espacio para botones
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
            Text(paciente.persona.email, modifier = Modifier.weight(3f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(paciente.persona.telefono, modifier = Modifier.weight(2f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(paciente.persona.direccion, modifier = Modifier.weight(2f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(paciente.persona.fechaNacimiento, modifier = Modifier.weight(1.5f), maxLines = 1, overflow = TextOverflow.Ellipsis)

            Button(
                onClick = { onEditarClick(paciente) },
                modifier = Modifier.widthIn(min = 60.dp, max = 80.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Modificar", style = MaterialTheme.typography.labelSmall)
            }

            Button(
                onClick = { onEliminarClick(paciente) },
                modifier = Modifier.widthIn(min = 60.dp, max = 80.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar", style = MaterialTheme.typography.labelSmall)
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
