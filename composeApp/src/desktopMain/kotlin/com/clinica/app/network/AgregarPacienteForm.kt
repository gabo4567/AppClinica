package com.clinica.app.network

import RegistroPacienteDTO
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun AgregarPacienteForm(
    onConfirmar: (RegistroPacienteDTO) -> Unit,
    onCancelar: () -> Unit
) {
    var dni by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") } // formato "yyyy-MM-dd"
    var obraSocial by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

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
        // Podés agregar más validaciones si querés
        errorMessage = null
        return true
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = dni,
            onValueChange = { dni = it },
            label = { Text("DNI") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = apellido,
            onValueChange = { apellido = it },
            label = { Text("Apellido") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            maxLines = 2
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = fechaNacimiento,
            onValueChange = { fechaNacimiento = it },
            label = { Text("Fecha de Nacimiento (yyyy-MM-dd)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = obraSocial,
            onValueChange = { obraSocial = it },
            label = { Text("Obra Social") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = onCancelar,
                modifier = Modifier
                    .height(40.dp)
                    .widthIn(min = 100.dp), // ancho mínimo para igualar tamaño
                border = BorderStroke(1.dp, Color(0x4D000000)) // contorno negro suave
            ) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (validarCampos()) {
                    val nuevoPaciente = RegistroPacienteDTO(
                        dni = dni.trim(),
                        nombre = nombre.trim(),
                        apellido = apellido.trim(),
                        email = email.trim(),
                        telefono = telefono.trim(),
                        direccion = direccion.trim(),
                        fechaNacimiento = fechaNacimiento.trim(),
                        obraSocial = obraSocial.trim(),
                        idRol = 4,              // Fijo para paciente
                        idEspecialidad = null,  // Null para paciente
                        idEstadoPersona = 1,    // Activo
                        idEstadoPaciente = 1    // Activo
                    )
                    onConfirmar(nuevoPaciente)
                }
            },
                modifier = Modifier.height(40.dp)
            ) {
                Text("Confirmar")
            }
        }

    }
}
