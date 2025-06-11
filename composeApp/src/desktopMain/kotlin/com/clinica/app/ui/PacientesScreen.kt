package com.clinica.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
            LazyColumn {
                items(pacientes) { paciente ->
                    PacienteItem(paciente)
                }
            }
        }
    }
}

@Composable
fun PacienteItem(paciente: Paciente) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("${paciente.persona.nombre} ${paciente.persona.apellido}", style = MaterialTheme.typography.titleMedium)
            Text("Obra social: ${paciente.obraSocial}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
