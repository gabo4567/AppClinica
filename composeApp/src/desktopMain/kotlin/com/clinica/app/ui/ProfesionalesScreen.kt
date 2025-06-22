package com.clinica.app.ui

import ProfesionalDTO
import com.clinica.app.network.ProfesionalService
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import com.clinica.app.network.AgregarProfesionalForm
import com.clinica.app.network.EditarProfesionalForm
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val especialidadesMap = mapOf(
    1L to "Clínica General",
    2L to "Pediatría",
    3L to "Cardiología",
    4L to "Ginecología"
)

@Composable
fun ProfesionalesScreen() {
    var estadoFiltro by remember { mutableStateOf<Long?>(null) } // null = todos, 1L = activo, 2L = inactivo
    var mostrarFormularioAgregar by remember { mutableStateOf(false) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }
    var eliminarErrorMessage by remember { mutableStateOf<String?>(null) }

    var mostrarFormularioEditar by remember { mutableStateOf(false) }
    var profesionalSeleccionado by remember { mutableStateOf<ProfesionalDTO?>(null) }

    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var profesionalAEliminar by remember { mutableStateOf<ProfesionalDTO?>(null) }
    var isProcessingEliminar by remember { mutableStateOf(false) }

    var filtroDniProfesional by remember { mutableStateOf("") }
    var filtroEspecialidad by remember { mutableStateOf("Todos") }

    val especialidadesOpciones = listOf("Todos") + especialidadesMap.values.toList()

    val profesionales = remember { mutableStateListOf<ProfesionalDTO>() }

    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 0.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                color = Color(0xFF1976D2),
                shadowElevation = 4.dp,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Gestión de Profesionales de la Clínica",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // FILTRO POR ESTADO + Botón Agregar alineado a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Filtrar profesionales por estado: ",
                    modifier = Modifier.padding(end = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Button(
                    onClick = { estadoFiltro = null },
                    colors = if (estadoFiltro == null)
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2), // azul fuerte (activo)
                            contentColor = Color.Black
                        )
                    else
                        ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFBBDEFB), // azul clarito (inactivo)
                            contentColor = Color.Black
                        ),
                    border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.3f))
                ) {
                    Text("Todos", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.width(5.dp))

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

                Spacer(modifier = Modifier.width(5.dp))

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

                Spacer(modifier = Modifier.width(5.dp))

                OutlinedTextField(
                    value = filtroDniProfesional,
                    onValueChange = { filtroDniProfesional = it },
                    label = { Text("Filtrar Profesional (DNI)") },
                    modifier = Modifier
                        .width(225.dp)
                        .padding(start = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    readOnly = false
                )

                Spacer(modifier = Modifier.width(5.dp))

                DropdownFiltro(
                    label = "Filtrar por Especialidad",
                    opciones = especialidadesOpciones,
                    seleccion = filtroEspecialidad,
                    onSeleccion = { filtroEspecialidad = it },
                    modifier = Modifier
                        .width(200.dp)
                        .padding(start = 8.dp)
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
                    Text("Agregar Profesional", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            ProfesionalesHeader()

            // Cargar los profesionales al iniciar la pantalla
            LaunchedEffect(Unit) {
                try {
                    val lista = ProfesionalService.getProfesionales()
                    profesionales.clear()
                    profesionales.addAll(lista)
                } catch (e: Exception) {
                    eliminarErrorMessage = "Error al cargar profesionales: ${e.message}"
                }
            }

            // Aplicar filtro de estado si corresponde
            val profesionalesFiltrados = profesionales.filter { profesional ->
                val cumpleEstado = estadoFiltro == null || profesional.idEstado == estadoFiltro

                val cumpleDni = filtroDniProfesional.isBlank() || profesional.dni.contains(filtroDniProfesional, ignoreCase = true)

                val nombreEspecialidad = profesional.idEspecialidad?.let { especialidadesMap[it] } ?: ""

                val cumpleEspecialidad = filtroEspecialidad == "Todos" || nombreEspecialidad == filtroEspecialidad

                cumpleEstado && cumpleDni && cumpleEspecialidad
            }

            // Renderizar lista de profesionales
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(profesionalesFiltrados) { profesional ->
                    ProfesionalItem(
                        profesional = profesional,
                        onEditarClick = { seleccionado ->
                            profesionalSeleccionado = seleccionado
                            mostrarFormularioEditar = true
                        },
                        onEliminarClick = { seleccionado ->
                            profesionalAEliminar = seleccionado
                            mostrarDialogoEliminar = true
                        }

                    )
                }
            }

            // Mensajes de error eliminar
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

        // Mensaje éxito centrado
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
                        //containerColor = Color(0xFF4CAF50)
                    ) {
                        Text(mensaje //, color = Color.White
                        )
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
            Dialog(onDismissRequest = { mostrarFormularioAgregar = false }) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AgregarProfesionalForm(
                        onConfirmar = { nuevoProfesional ->
                            scope.launch {
                                try {
                                    ProfesionalService.crearProfesional(nuevoProfesional)
                                    mensajeExito = "Profesional agregado con éxito"
                                    mostrarFormularioAgregar = false
                                    // Recargar la lista
                                    val listaActualizada = ProfesionalService.getProfesionales()
                                    profesionales.clear()
                                    profesionales.addAll(listaActualizada)
                                } catch (e: Exception) {
                                    eliminarErrorMessage = "Error al agregar profesional: ${e.message}"
                                }
                            }
                        },
                        onCancelar = {
                            mostrarFormularioAgregar = false
                        }
                    )
                }
            }
        }

        if (mostrarFormularioEditar && profesionalSeleccionado != null) {
            Dialog(onDismissRequest = {
                mostrarFormularioEditar = false
                profesionalSeleccionado = null
            }) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .padding(30.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp) // padding interior para el contenido
                    ) {
                        Text(
                            text = "Modificar Profesional",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        EditarProfesionalForm(
                            profesional = profesionalSeleccionado!!,
                            onConfirmar = { profesionalEditado ->
                                scope.launch {
                                    try {
                                        // Si estaba inactivo (2), lo activamos (1)
                                        val profesionalEditadoConEstado = if (profesionalEditado.idEstado == 2L) {
                                            profesionalEditado.copy(idEstado = 1L)
                                        } else {
                                            profesionalEditado
                                        }

                                        ProfesionalService.actualizarProfesional(profesionalEditadoConEstado.id, profesionalEditadoConEstado)
                                        mensajeExito = "Profesional actualizado con éxito"
                                        mostrarFormularioEditar = false
                                        profesionalSeleccionado = null
                                        val listaActualizada = ProfesionalService.getProfesionales()
                                        profesionales.clear()
                                        profesionales.addAll(listaActualizada)
                                    } catch (e: Exception) {
                                        eliminarErrorMessage = "Error al modificar profesional: ${e.message}"
                                    }
                                }
                            },
                            onCancelar = {
                                mostrarFormularioEditar = false
                                profesionalSeleccionado = null
                            }
                        )
                    }
                }
            }
        }

        if (mostrarDialogoEliminar && profesionalAEliminar != null) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoEliminar = false },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Estás seguro de que deseas eliminar al profesional?") },
                confirmButton = {
                    TextButton(onClick = {
                        eliminarErrorMessage = null
                        isProcessingEliminar = true
                        mostrarDialogoEliminar = false

                        println("Intentando desactivar profesional con ID: ${profesionalAEliminar?.id}")

                        scope.launch {
                            try {
                                // Copiamos el profesional cambiando el estado a 2 (inactivo)
                                val profesionalInactivo = profesionalAEliminar!!.copy(idEstado = 2)
                                // Llamamos a actualizar profesional con el nuevo estado
                                ProfesionalService.actualizarProfesional(profesionalInactivo.id, profesionalInactivo)
                                mensajeExito = "Profesional eliminado con éxito"
                                val listaActualizada = ProfesionalService.getProfesionales()
                                profesionales.clear()
                                profesionales.addAll(listaActualizada)
                            } catch (e: Exception) {
                                eliminarErrorMessage = "Error al eliminar profesional: ${e.message}"
                                e.printStackTrace()
                            } finally {
                                isProcessingEliminar = false
                                profesionalAEliminar = null
                            }
                        }
                    }) {
                        Text("Confirmar", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        mostrarDialogoEliminar = false
                        profesionalAEliminar = null
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }


    }
}

@Composable
fun ProfesionalesHeader() {
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
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "DNI",
                modifier = Modifier.padding(start = 15.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                "Nombre",
                modifier = Modifier.padding(start = 70.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
            Text(
                "Apellido",
                modifier = Modifier.padding(start = 60.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
            Text(
                "Email",
                modifier = Modifier.padding(start = 140.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
            Text(
                "Teléfono",
                modifier = Modifier.padding(start = 170.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                "Especialidad",
                modifier = Modifier.padding(start = 100.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
            Text(
                "Fecha Nacimiento",
                modifier = Modifier.padding(start = 120.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(0.dp))
        }
    }
}

@Composable
fun ProfesionalItem(
    profesional: ProfesionalDTO,
    onEditarClick: (ProfesionalDTO) -> Unit,
    onEliminarClick: (ProfesionalDTO) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(profesional.dni, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(profesional.nombre, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(profesional.apellido, modifier = Modifier.weight(1.4f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(profesional.email, modifier = Modifier.weight(2.4f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(profesional.telefono, modifier = Modifier.weight(1.5f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            val nombreEspecialidad = profesional.idEspecialidad?.let { especialidadesMap[it] } ?: "-"
            Text(nombreEspecialidad, modifier = Modifier.weight(2f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(profesional.fechaNacimiento.toString(), modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)

            Spacer(modifier = Modifier.weight(0.5f))

            Button(
                onClick = { onEditarClick(profesional) },
                modifier = Modifier.padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                Text("Modificar")
            }

            Button(
                onClick = { onEliminarClick(profesional) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text("Eliminar")
            }
        }
    }
}