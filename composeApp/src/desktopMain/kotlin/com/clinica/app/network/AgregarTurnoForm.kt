package com.clinica.app.network

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
// import kotlinx.datetime.LocalDateTime
import ProfesionalDTO
import androidx.compose.ui.text.style.TextAlign
import com.clinica.app.models.Paciente

// Mapa que relaciona nombres de especialidades con sus IDs
val especialidadesMap = mapOf(
    "Clínica General" to 1L,
    "Pediatría" to 2L,
    "Cardiología" to 3L,
    "Ginecología" to 4L
)

@Composable
fun AgregarTurnoForm(
    pacientes: List<Paciente>,
    profesionales: List<ProfesionalDTO>,
    onConfirmar: (RegistroTurnoDTO) -> Unit,
    onCancelar: () -> Unit,
    obtenerFechasDisponiblesApi: suspend (idProfesional: Long) -> List<String>,
    obtenerHorariosDisponiblesApi: suspend (idProfesional: Long, fecha: String) -> List<String>
) {
    Dialog(onDismissRequest = onCancelar) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth(1f).heightIn(min = 600.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {

                Text(
                    text = "Crear Nuevo Turno",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                var dniBusqueda by remember { mutableStateOf("") }
                var pacienteSeleccionado by remember { mutableStateOf<Paciente?>(null) }

                var especialidadSeleccionada by remember { mutableStateOf("") }
                var profesionalSeleccionado by remember { mutableStateOf<ProfesionalDTO?>(null) }

                var fechaSeleccionada by remember { mutableStateOf("") }
                var horaSeleccionada by remember { mutableStateOf("") }

                var fechasDisponibles by remember { mutableStateOf<List<String>>(emptyList()) }
                var horariosDisponibles by remember { mutableStateOf<List<String>>(emptyList()) }

                val especialidadesDisponibles = especialidadesMap.keys.toList()
                val idEspecialidadSeleccionada = especialidadesMap[especialidadSeleccionada]
                val profesionalesFiltrados = profesionales.filter { it.idEspecialidad == idEspecialidadSeleccionada }
                val pacientesFiltrados = pacientes.filter { it.persona.dni == dniBusqueda }

                if (pacientesFiltrados.isNotEmpty()) {
                    pacienteSeleccionado = pacientesFiltrados.first()
                }

                LaunchedEffect(profesionalSeleccionado) {
                    fechaSeleccionada = ""
                    horaSeleccionada = ""
                    horariosDisponibles = emptyList()
                    if (profesionalSeleccionado != null) {
                        fechasDisponibles = try {
                            obtenerFechasDisponiblesApi(profesionalSeleccionado!!.id)
                        } catch (e: Exception) {
                            emptyList()
                        }
                    } else {
                        fechasDisponibles = emptyList()
                    }
                }

                LaunchedEffect(profesionalSeleccionado, fechaSeleccionada) {
                    horaSeleccionada = ""
                    if (profesionalSeleccionado != null && fechaSeleccionada.isNotEmpty()) {
                        horariosDisponibles = try {
                            obtenerHorariosDisponiblesApi(profesionalSeleccionado!!.id, fechaSeleccionada)
                        } catch (e: Exception) {
                            emptyList()
                        }
                    } else {
                        horariosDisponibles = emptyList()
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = dniBusqueda,
                        onValueChange = { dniBusqueda = it },
                        label = { Text("DNI") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = pacienteSeleccionado?.persona?.nombre ?: "",
                        onValueChange = {},
                        label = { Text("Nombre") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = pacienteSeleccionado?.persona?.apellido ?: "",
                        onValueChange = {},
                        label = { Text("Apellido") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = pacienteSeleccionado?.persona?.telefono ?: "",
                        onValueChange = {},
                        label = { Text("Teléfono") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Agrupamos los 4 Dropdowns en una columna con dos filas
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    // Fila 1: Especialidad y Profesional con padding ajustable
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
                            modifier = Modifier
                                .padding(end = 4.dp) // puedes cambiar este valor
                                .width(260.dp)       // ancho ajustable
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
                            modifier = Modifier
                                .padding(start = 4.dp) // puedes cambiar este valor
                                .width(260.dp)         // ancho ajustable
                        )
                    }

                    // Fila 2: Fecha y Horario con padding ajustable
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DropdownSelector(
                            label = "Fecha",
                            opciones = fechasDisponibles,
                            seleccion = fechaSeleccionada,
                            onSeleccion = { fechaSeleccionada = it },
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .width(260.dp) // ajustalo según necesites
                        )

                        DropdownSelector(
                            label = "Horario",
                            opciones = horariosDisponibles,
                            seleccion = horaSeleccionada,
                            onSeleccion = { horaSeleccionada = it },
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .width(260.dp) // ajustalo según necesites
                        )
                    }
                }


                // Espaciado final
                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onCancelar) { Text("Cancelar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val paciente = pacienteSeleccionado
                            val profesional = profesionalSeleccionado
                            if (paciente != null && profesional != null && fechaSeleccionada.isNotBlank() && horaSeleccionada.isNotBlank()) {
                                val fechaHora = "${fechaSeleccionada}T${horaSeleccionada}" // <-- CAMBIO CLAVE
                                val turno = RegistroTurnoDTO(
                                    idPaciente = paciente.persona.id,
                                    idProfesional = profesional.idPersona,
                                    fechaHora = fechaHora, // <-- ahora es String
                                    duracion = 30,
                                    idEstado = 10,
                                    observaciones = null
                                )
                                onConfirmar(turno)
                            }
                        }
                    ) { Text("Confirmar") }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    opciones: List<String>,
    seleccion: String,
    onSeleccion: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = seleccion,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(text = { Text(opcion) }, onClick = {
                    onSeleccion(opcion)
                    expanded = false
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownProfesionalSelector(
    label: String,
    opciones: List<ProfesionalDTO>,
    seleccion: ProfesionalDTO?,
    onSeleccion: (ProfesionalDTO) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = seleccion?.let { "${it.nombre} ${it.apellido}" } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = modifier.menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            opciones.forEach { profesional ->
                DropdownMenuItem(
                    text = { Text("${profesional.nombre} ${profesional.apellido}") },
                    onClick = {
                        onSeleccion(profesional)
                        expanded = false
                    }
                )
            }
        }
    }
}

