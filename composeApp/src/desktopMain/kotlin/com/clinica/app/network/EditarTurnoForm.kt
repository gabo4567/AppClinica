package com.clinica.app.network

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ProfesionalDTO
import androidx.compose.ui.window.Dialog
import com.clinica.app.models.TurnoDTO
import java.time.format.DateTimeFormatter

@Composable
fun EditarTurnoForm(
    turnoOriginal: TurnoDTO,
    profesionalDelTurno: ProfesionalDTO,
    profesionales: List<ProfesionalDTO>,
    onConfirmar: (EditarTurnoDTO) -> Unit,
    onCancelar: () -> Unit,
    obtenerFechasDisponiblesApi: suspend (idProfesional: Long) -> List<String>,
    obtenerHorariosDisponiblesApi: suspend (idProfesional: Long, fecha: String) -> List<String>
) {
    Dialog(onDismissRequest = onCancelar) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 600.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                Text(
                    text = "Modificar Turno",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                // Estados del formulario
                var especialidadSeleccionada by remember {
                    mutableStateOf(
                        especialidadesMap.entries.find { it.value == profesionalDelTurno.idEspecialidad }?.key ?: ""
                    )
                }

                var profesionalSeleccionado by remember { mutableStateOf<ProfesionalDTO?>(profesionalDelTurno) }


                var fechasDisponibles by remember { mutableStateOf<List<String>>(emptyList()) }
                var horariosDisponibles by remember { mutableStateOf<List<String>>(emptyList()) }

                val fechaOriginal = turnoOriginal.fechaHora.toLocalDate().toString()
                val horaOriginal = turnoOriginal.fechaHora.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))

                var fechaSeleccionada by remember { mutableStateOf(fechaOriginal) }
                var horaSeleccionada by remember { mutableStateOf(horaOriginal) }

                val especialidadesDisponibles = especialidadesMap.keys.toList()
                val idEspecialidadSeleccionada = especialidadesMap[especialidadSeleccionada]
                val profesionalesFiltrados = profesionales.filter { it.idEspecialidad == idEspecialidadSeleccionada }

                // Al cambiar profesional, cargar fechas
                LaunchedEffect(profesionalSeleccionado) {
                    if (profesionalSeleccionado != null) {
                        fechasDisponibles = try {
                            obtenerFechasDisponiblesApi(profesionalSeleccionado!!.id)
                        } catch (e: Exception) {
                            emptyList()
                        }
                    } else {
                        fechasDisponibles = emptyList()
                    }
                    fechaSeleccionada = ""
                    horaSeleccionada = ""
                    horariosDisponibles = emptyList()
                }

                // Al cambiar fecha, cargar horarios
                LaunchedEffect(profesionalSeleccionado, fechaSeleccionada) {
                    if (profesionalSeleccionado != null && fechaSeleccionada.isNotBlank()) {
                        horariosDisponibles = try {
                            obtenerHorariosDisponiblesApi(profesionalSeleccionado!!.id, fechaSeleccionada)
                        } catch (e: Exception) {
                            emptyList()
                        }
                    } else {
                        horariosDisponibles = emptyList()
                    }
                    horaSeleccionada = ""
                }

                // Campos
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    DropdownSelector(
                        label = "Especialidad",
                        opciones = especialidadesDisponibles,
                        seleccion = especialidadSeleccionada,
                        onSeleccion = {
                            especialidadSeleccionada = it
                            profesionalSeleccionado = null
                            fechaSeleccionada = ""
                            horaSeleccionada = ""
                            fechasDisponibles = emptyList()
                            horariosDisponibles = emptyList()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownProfesionalSelector(
                        label = "Profesional",
                        opciones = profesionalesFiltrados,
                        seleccion = profesionalSeleccionado,
                        onSeleccion = {
                            profesionalSeleccionado = it
                            fechaSeleccionada = ""
                            horaSeleccionada = ""
                            fechasDisponibles = emptyList()
                            horariosDisponibles = emptyList()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownSelector(
                        label = "Fecha",
                        opciones = fechasDisponibles,
                        seleccion = fechaSeleccionada,
                        onSeleccion = { fechaSeleccionada = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownSelector(
                        label = "Horario",
                        opciones = horariosDisponibles,
                        seleccion = horaSeleccionada,
                        onSeleccion = { horaSeleccionada = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onCancelar) { Text("Cancelar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val profesional = profesionalSeleccionado
                            if (profesional != null && fechaSeleccionada.isNotBlank() && horaSeleccionada.isNotBlank()) {
                                val fechaHora = "${fechaSeleccionada}T${horaSeleccionada}"
                                val turnoModificado = EditarTurnoDTO(
                                    id = turnoOriginal.id!!,
                                    idPaciente = turnoOriginal.idPaciente,
                                    idProfesional = profesional.idPersona,
                                    fechaHora = fechaHora,
                                    duracion = 30,
                                    idEstado = 10,
                                    observaciones = turnoOriginal.observaciones
                                )

                                onConfirmar(turnoModificado)
                            }
                        }
                    ) { Text("Confirmar") }
                }
            }
        }
    }
}
