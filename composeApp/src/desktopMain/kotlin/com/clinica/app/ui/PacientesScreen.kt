package com.clinica.app.ui

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

@Composable
fun PacientesScreen() {
    var pacientes by remember { mutableStateOf<List<Paciente>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("Pacientes", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    CircularProgressIndicator()
                } else if (errorMessage != null) {
                    Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                } else {
                    PacientesHeader() // <-- acá ponés el encabezado
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn {
                        items(pacientes) { paciente ->
                            PacienteItem(
                                paciente = paciente,
                                onEditarClick = { /* TODO */ },
                                onEliminarClick = { /* TODO */ }
                            )
                        }
                    }
                }
            }

            LazyColumn {
                items(pacientes) { paciente ->
                    PacienteItem(
                        paciente = paciente,
                        onEditarClick = { /* TODO: acción editar */ },
                        onEliminarClick = { /* TODO: acción eliminar */ }
                    )
                }
            }

        }
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
            Text("Email", modifier = Modifier.weight(2.5f), style = MaterialTheme.typography.labelMedium)
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


