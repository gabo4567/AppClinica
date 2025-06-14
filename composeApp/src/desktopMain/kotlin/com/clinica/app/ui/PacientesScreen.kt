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

    // Estado para mostrar el diálogo de edición
    var pacienteAEditar by remember { mutableStateOf<Paciente?>(null) }
    var editErrorMessage by remember { mutableStateOf<String?>(null) }
    var isProcessingEdit by remember { mutableStateOf(false) }

    // Estado para mensajes de eliminación
    var eliminarErrorMessage by remember { mutableStateOf<String?>(null) }
    var isProcessingEliminar by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    // Cargar pacientes al iniciar
    LaunchedEffect(Unit) {
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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Pacientes", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage != null) {
            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
        } else {
            PacientesHeader()
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(pacientes) { paciente ->
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
                                    // Usar el ID de paciente, no el ID de persona
                                    PacienteService.eliminarPaciente(it.id)
                                    // Refrescar lista tras eliminar
                                    pacientes = PacienteService.getPacientes()
                                } catch (e: Exception) {
                                    eliminarErrorMessage = "Error al eliminar paciente: ${e.message}"
                                    e.printStackTrace() // Para que salga en consola el detalle del error
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

    // Diálogo de edición de paciente
    if (pacienteAEditar != null) {
        AlertDialog(
            onDismissRequest = { if (!isProcessingEdit) pacienteAEditar = null },
            title = { Text("Editar Paciente") },
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
                                    // Mapear Paciente a DTO para API
                                    val dto = pacienteActualizado.toRegistroPacienteDTO()
                                    PacienteService.actualizarPaciente(pacienteActualizado.id, dto)
                                    // Refrescar lista después de actualizar
                                    pacientes = PacienteService.getPacientes()
                                    pacienteAEditar = null // cerrar diálogo
                                } catch (e: Exception) {
                                    editErrorMessage = "Error al actualizar paciente: ${e.message}"
                                    e.printStackTrace() // Para que salga en consola el detalle del error
                                }
                                finally {
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

    // Mostrar error de eliminación si existe
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

// Header de la tabla
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
            Text("Email", modifier = Modifier.weight(2.5f), style = MaterialTheme.typography.labelMedium)
            Text("Teléfono", modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelMedium)
            Text("Dirección", modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelMedium)
            Text("Fecha Nac.", modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.width(160.dp)) // espacio para botones
        }
    }
}

// Fila de paciente con botones editar y eliminar
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
            Text(paciente.persona.telefono, modifier = Modifier.weight(2f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(paciente.persona.direccion, modifier = Modifier.weight(2f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(paciente.persona.fechaNacimiento, modifier = Modifier.weight(1.5f), maxLines = 1, overflow = TextOverflow.Ellipsis)

            Button(
                onClick = { onEditarClick(paciente) },
                modifier = Modifier.widthIn(min = 60.dp, max = 80.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Editar", style = MaterialTheme.typography.labelSmall)
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
 * Función auxiliar para mapear Paciente a RegistroPacienteDTO,
 * debe existir en el modelo o crearla aquí.
 */
fun Paciente.toRegistroPacienteDTO(): RegistroPacienteDTO {
    return RegistroPacienteDTO(
        dni = persona.dni,
        nombre = persona.nombre,
        apellido = persona.apellido,
        email = persona.email,
        telefono = persona.telefono,
        direccion = persona.direccion,
        fechaNacimiento = persona.fechaNacimiento, // Asegurate que venga como "yyyy-MM-dd"
        obraSocial = obraSocial,
        idRol = 4,
        idEspecialidad = null,
        idEstadoPersona = 1,
        idEstadoPaciente = 1
    )
}

