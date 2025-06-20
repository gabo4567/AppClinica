package com.clinica.app.network

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import kotlinx.datetime.LocalDate

import ProfesionalDTO
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.DropdownMenuItem

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun EditarProfesionalForm(
    profesional: ProfesionalDTO,
    onConfirmar: (ProfesionalDTO) -> Unit,
    onCancelar: () -> Unit
) {
    var dni by remember { mutableStateOf(profesional.dni) }
    var nombre by remember { mutableStateOf(profesional.nombre) }
    var apellido by remember { mutableStateOf(profesional.apellido) }
    var email by remember { mutableStateOf(profesional.email) }
    var telefono by remember { mutableStateOf(profesional.telefono) }
    var direccion by remember { mutableStateOf(profesional.direccion) }
    var fechaNacimiento by remember { mutableStateOf(profesional.fechaNacimiento.toString()) }
    var matriculaProfesional by remember { mutableStateOf(profesional.matriculaProfesional) }
    var especialidadSeleccionada by remember { mutableStateOf(profesional.idEspecialidad ?: 1L) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val especialidades = listOf(
        1L to "Clínica General",
        2L to "Pediatría",
        3L to "Cardiología",
        4L to "Ginecología"
    )

    fun validarCampos(): Boolean {
        if (dni.isBlank() || nombre.isBlank() || apellido.isBlank() || email.isBlank() || matriculaProfesional.isBlank() || fechaNacimiento.isBlank()) {
            errorMessage = "Todos los campos obligatorios deben estar completos"
            return false
        }
        errorMessage = null
        return true
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(value = dni, onValueChange = { dni = it }, label = { Text("DNI") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = direccion, onValueChange = { direccion = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = fechaNacimiento, onValueChange = { fechaNacimiento = it }, label = { Text("Fecha de nacimiento (yyyy-MM-dd)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = matriculaProfesional, onValueChange = { matriculaProfesional = it }, label = { Text("Matrícula profesional") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown
        var expanded by remember { mutableStateOf(false) }
        val especialidadNombre = especialidades.find { it.first == especialidadSeleccionada }?.second ?: ""
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = especialidadNombre,
                onValueChange = {},
                readOnly = true,
                label = { Text("Especialidad") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
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

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
        }

        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (validarCampos()) {
                    val dtoEditado = profesional.copy(
                        dni = dni.trim(),
                        nombre = nombre.trim(),
                        apellido = apellido.trim(),
                        email = email.trim(),
                        telefono = telefono.trim(),
                        direccion = direccion.trim(),
                        fechaNacimiento = LocalDate.parse(fechaNacimiento.trim()),
                        idEspecialidad = especialidadSeleccionada,
                        matriculaProfesional = matriculaProfesional.trim()
                    )
                    onConfirmar(dtoEditado)
                }
            }) {
                Text("Guardar cambios")
            }
        }
    }
}
