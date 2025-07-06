package com.clinica.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.clinica.app.models.CantidadTurnosPorDiaDTO
import com.clinica.app.network.ReporteApi
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.clinica.app.models.CantidadTurnosPorProfesionalDTO
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.sp
import com.clinica.app.models.PacientesAtendidosPorEspecialidadDTO

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun InformesScreen() {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val azulOscuro = Color(0xFF1565C0)
    val especialidades = listOf("Todas", "Clínica General", "Pediatría", "Cardiología", "Ginecología")
    var especialidadSeleccionada by remember { mutableStateOf("Todas") }
    var expandedEspecialidad by remember { mutableStateOf(false) }


    // Variables para el primer informe
    var resultadosTurnosPorDia by remember { mutableStateOf<List<CantidadTurnosPorDiaDTO>>(emptyList()) }
    var errorTurnosPorDia by remember { mutableStateOf<String?>(null) }
    var fechaInicioTexto by remember { mutableStateOf(LocalDate.now().minusDays(7).toString()) }
    var fechaFinTexto by remember { mutableStateOf(LocalDate.now().toString()) }

    // Variables para el segundo informe
    var fechaInicioProfTexto by remember { mutableStateOf(LocalDate.now().minusDays(7).toString()) }
    var fechaFinProfTexto by remember { mutableStateOf(LocalDate.now().toString()) }
    var filtroNombreProf by remember { mutableStateOf("") }  // Para el filtro por nombre o apellido
    var resultadosTurnosPorProfesional by remember { mutableStateOf<List<CantidadTurnosPorProfesionalDTO>>(emptyList()) }
    var errorTurnosPorProfesional by remember { mutableStateOf<String?>(null) }

    // Variables para el tercer informe
    var filtroEspecialidad by remember { mutableStateOf("") }
    var fechaInicioEspecialidad by remember { mutableStateOf(LocalDate.now().minusDays(7).toString()) }
    var fechaFinEspecialidad by remember { mutableStateOf(LocalDate.now().toString()) }
    var resultadosPacientesPorEspecialidad by remember { mutableStateOf<List<PacientesAtendidosPorEspecialidadDTO>>(emptyList()) }
    var errorPacientesPorEspecialidad by remember { mutableStateOf<String?>(null) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(top = 1.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        // ---------- BANNER SUPERIOR ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .background(azulOscuro, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Informes de la Clínica",
                style = MaterialTheme.typography.h5.copy( // Estilo base
                    color = Color.White,
                    fontSize = 30.sp // Tamaño personalizado, un poco más chico que h4
                ),
                modifier = Modifier.align(Alignment.Center)
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---------- INFORME 1 ----------
        Text("Informe: Cantidad de Turnos por Día", style = MaterialTheme.typography.h6)

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = fechaInicioTexto,
                onValueChange = { fechaInicioTexto = it },
                label = { Text("Fecha inicio") },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = fechaFinTexto,
                onValueChange = { fechaFinTexto = it },
                label = { Text("Fecha fin") },
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    scope.launch {
                        errorTurnosPorDia = null
                        try {
                            val fechaInicio = LocalDate.parse(fechaInicioTexto)
                            val fechaFin = LocalDate.parse(fechaFinTexto)
                            resultadosTurnosPorDia = ReporteApi.obtenerCantidadTurnosPorDia(fechaInicio, fechaFin)
                        } catch (e: Exception) {
                            errorTurnosPorDia = "Error al obtener datos: ${e.message}"
                            resultadosTurnosPorDia = emptyList()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = azulOscuro)
            ) {
                Text("Buscar", color = Color.White)
            }


        }

        if (errorTurnosPorDia != null) {
            Text(errorTurnosPorDia!!, color = MaterialTheme.colors.error)
        }

        if (resultadosTurnosPorDia.isNotEmpty()) {

            Spacer(modifier = Modifier.height(12.dp)) // 🔹 Más lejos de los campos de fecha

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Fecha del Turno",
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(end = 555.dp)
                )
                Text(
                    text = "Cantidad de Turnos",
                    style = MaterialTheme.typography.subtitle2
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .padding(top = 4.dp) // 🔹 Más cerca del encabezado
            ) {
                items(resultadosTurnosPorDia) { item ->
                    Row(Modifier.fillMaxWidth().padding(8.dp)) {
                        Text(item.fecha.toString(), modifier = Modifier.weight(1f))
                        Text(item.cantidad.toString(), modifier = Modifier.weight(1f))
                    }
                    Divider()
                }
            }
        }


        Spacer(modifier = Modifier.height(32.dp))

        Text("Informe: Cantidad de Turnos por Profesional", style = MaterialTheme.typography.h6)

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = filtroNombreProf,
                onValueChange = { filtroNombreProf = it },
                label = { Text("Nombre o Apellido (opcional)") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = fechaInicioProfTexto,
                onValueChange = { fechaInicioProfTexto = it },
                label = { Text("Fecha inicio") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = fechaFinProfTexto,
                onValueChange = { fechaFinProfTexto = it },
                label = { Text("Fecha fin") },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    scope.launch {
                        errorTurnosPorProfesional = null
                        try {
                            val fechaInicio = LocalDate.parse(fechaInicioProfTexto)
                            val fechaFin = LocalDate.parse(fechaFinProfTexto)
                            resultadosTurnosPorProfesional =
                                ReporteApi.obtenerCantidadTurnosPorProfesional(fechaInicio, fechaFin, filtroNombreProf)
                        } catch (e: Exception) {
                            errorTurnosPorProfesional = "Error al obtener datos: ${e.message}"
                            resultadosTurnosPorProfesional = emptyList()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = azulOscuro)
            ) {
                Text("Buscar", color = Color.White)
            }

        }

        if (errorTurnosPorProfesional != null) {
            Text(errorTurnosPorProfesional!!, color = MaterialTheme.colors.error)
        }

        if (resultadosTurnosPorProfesional.isNotEmpty()) {

            Spacer(modifier = Modifier.height(12.dp)) // 🔹 Aleja encabezado de los filtros

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Profesional",
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(end = 585.dp) // 🔹 Espacio entre columnas
                )
                Text(
                    text = "Cantidad de Turnos",
                    style = MaterialTheme.typography.subtitle2
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .padding(top = 4.dp) // 🔹 Encabezado más cerca de los datos
            ) {
                items(resultadosTurnosPorProfesional) { item ->
                    Row(Modifier.fillMaxWidth().padding(8.dp)) {
                        Text(item.nombreProfesional, modifier = Modifier.weight(1f))
                        Text(item.cantidadTurnos.toString(), modifier = Modifier.weight(1f))
                    }
                    Divider()
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Informe: Pacientes Atendidos por Especialidad", style = MaterialTheme.typography.h6)

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ExposedDropdownMenuBox(
                expanded = expandedEspecialidad,
                onExpandedChange = { expandedEspecialidad = !expandedEspecialidad },
                modifier = Modifier.weight(1f) // Hace que el dropdown ocupe el mismo espacio que los otros campos
            ) {
                OutlinedTextField(
                    value = especialidadSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Especialidad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEspecialidad) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.fillMaxWidth() // Asegura que el campo ocupe el ancho completo dentro del Box
                )
                ExposedDropdownMenu(
                    expanded = expandedEspecialidad,
                    onDismissRequest = { expandedEspecialidad = false }
                ) {
                    especialidades.forEach { opcion ->
                        DropdownMenuItem(
                            onClick = {
                                especialidadSeleccionada = opcion
                                expandedEspecialidad = false
                            }
                        ) {
                            Text(opcion)
                        }
                    }
                }
            }


            OutlinedTextField(
                value = fechaInicioEspecialidad,
                onValueChange = { fechaInicioEspecialidad = it },
                label = { Text("Fecha inicio") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = fechaFinEspecialidad,
                onValueChange = { fechaFinEspecialidad = it },
                label = { Text("Fecha fin") },
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    scope.launch {
                        errorPacientesPorEspecialidad = null
                        try {
                            val fechaInicio = LocalDate.parse(fechaInicioEspecialidad)
                            val fechaFin = LocalDate.parse(fechaFinEspecialidad)

                            val especialidadParaFiltro = if (especialidadSeleccionada == "Todas") null else especialidadSeleccionada

                            resultadosPacientesPorEspecialidad =
                                ReporteApi.obtenerPacientesAtendidosPorEspecialidad(
                                    especialidadParaFiltro,
                                    fechaInicio,
                                    fechaFin
                                )
                        } catch (e: Exception) {
                            errorPacientesPorEspecialidad = "Error al obtener datos: ${e.message}"
                            resultadosPacientesPorEspecialidad = emptyList()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = azulOscuro)
            ) {
                Text("Buscar", color = Color.White)
            }


        }

        if (errorPacientesPorEspecialidad != null) {
            Text(errorPacientesPorEspecialidad!!, color = MaterialTheme.colors.error)
        }

        if (resultadosPacientesPorEspecialidad.isNotEmpty()) {

            Spacer(modifier = Modifier.height(12.dp)) // 🔹 Separa del formulario

            // Encabezados de la tabla
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Especialidad",
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(end = 575.dp) // Ajustar según ancho de pantalla
                )
                Text(
                    text = "Cantidad de Pacientes",
                    style = MaterialTheme.typography.subtitle2
                )
            }

            // Lista de resultados
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .padding(top = 4.dp)
            ) {
                items(resultadosPacientesPorEspecialidad) { item ->
                    Row(Modifier.fillMaxWidth().padding(8.dp)) {
                        Text(item.especialidad, modifier = Modifier.weight(1f))
                        Text(item.cantidadPacientes.toString(), modifier = Modifier.weight(1f))
                    }
                    Divider()
                }
            }
        }






    }
}
