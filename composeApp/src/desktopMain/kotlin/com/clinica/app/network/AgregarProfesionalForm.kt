package com.clinica.app.network

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import kotlinx.datetime.LocalDate

import ProfesionalDTO
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProfesionalForm(
    onConfirmar: (ProfesionalDTO) -> Unit,
    onCancelar: () -> Unit
) {
    var dni by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var matriculaProfesional by remember { mutableStateOf("") }
    var especialidadSeleccionada by remember { mutableStateOf(1L) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val especialidades = listOf(
        1L to "Clínica General",
        2L to "Pediatría",
        3L to "Cardiología",
        4L to "Ginecología"
    )

    var expanded by remember { mutableStateOf(false) }
    val especialidadNombre = especialidades.find { it.first == especialidadSeleccionada }?.second ?: ""

    fun esEmailValido(email: String): Boolean {
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.+)(\\.)(.+)$"
        return Regex(emailRegex).matches(email)
    }

    fun validarCampos(): Boolean {
        if (dni.isBlank()) {
            errorMessage = "El DNI es obligatorio"
            return false
        }
        if (nombre.isBlank()) {
            errorMessage = "El nombre es obligatorio"
            return false
        }
        if (apellido.isBlank()) {
            errorMessage = "El apellido es obligatorio"
            return false
        }
        if (email.isBlank() || !esEmailValido(email)) {
            errorMessage = "Email inválido"
            return false
        }
        if (matriculaProfesional.isBlank()) {
            errorMessage = "La matrícula profesional es obligatoria"
            return false
        }
        if (fechaNacimiento.isBlank()) {
            errorMessage = "La fecha de nacimiento es obligatoria"
            return false
        }
        errorMessage = null
        return true
    }

    Surface(
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Agregar Profesional",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            )
            {
                OutlinedTextField(value = dni, onValueChange = { dni = it }, label = { Text("DNI") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = fechaNacimiento, onValueChange = { fechaNacimiento = it }, label = { Text("Fecha de nacimiento (yyyy-MM-dd)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = matriculaProfesional, onValueChange = { matriculaProfesional = it }, label = { Text("Matrícula profesional") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                // Dropdown de especialidades
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = especialidadNombre,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Especialidad") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        especialidades.forEach { (id, nombre) ->
                            DropdownMenuItem(
                                text = { Text(nombre) },
                                onClick = {
                                    especialidadSeleccionada = id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                TextButton(onClick = onCancelar) {
                    Text("Cancelar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (validarCampos()) {
                            val dto = ProfesionalDTO(
                                id = 0,
                                idPersona = 0,
                                dni = dni.trim(),
                                nombre = nombre.trim(),
                                apellido = apellido.trim(),
                                email = email.trim(),
                                telefono = telefono.trim(),
                                direccion = direccion.trim(),
                                fechaNacimiento = LocalDate.parse(fechaNacimiento.trim()),
                                idRol = 3,
                                idEspecialidad = especialidadSeleccionada,
                                matriculaProfesional = matriculaProfesional.trim(),
                                idEstado = 1
                            )
                            onConfirmar(dto)
                        }
                    }
                ) {
                    Text("Confirmar")
                }
            }

        }
    }
}

