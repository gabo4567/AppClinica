package com.clinica.app.network

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.clinica.app.models.Paciente

@Composable
fun EditarPacienteForm(
    paciente: Paciente,
    onConfirmar: (Paciente) -> Unit,
    onCancelar: () -> Unit
) {
    // Campos de la persona
    var dni by remember { mutableStateOf(paciente.persona.dni) }
    var nombre by remember { mutableStateOf(paciente.persona.nombre) }
    var apellido by remember { mutableStateOf(paciente.persona.apellido) }
    var email by remember { mutableStateOf(paciente.persona.email) }
    var telefono by remember { mutableStateOf(paciente.persona.telefono) }
    var direccion by remember { mutableStateOf(paciente.persona.direccion) }
    var fechaNacimiento by remember { mutableStateOf(paciente.persona.fechaNacimiento) }

    // Campo del paciente
    var obraSocial by remember { mutableStateOf(paciente.obraSocial) }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {

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

        OutlinedTextField(value = obraSocial, onValueChange = { obraSocial = it }, label = { Text("Obra Social") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.End, // para alinearlos a la derecha
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Botón Cancelar primero, a la izquierda dentro del grupo
            OutlinedButton(onClick = onCancelar) {
                Text("Cancelar")
            }

            Spacer(modifier = Modifier.width(16.dp)) // espacio entre botones

            // Botón Confirmar a la derecha
            Button(onClick = {
                val personaActualizada = paciente.persona.copy(
                    dni = dni,
                    nombre = nombre,
                    apellido = apellido,
                    email = email,
                    telefono = telefono,
                    direccion = direccion,
                    fechaNacimiento = fechaNacimiento
                )

                val pacienteActualizado = paciente.copy(
                    persona = personaActualizada,
                    obraSocial = obraSocial
                )

                onConfirmar(pacienteActualizado)
            }) {
                Text("Confirmar")
            }
        }

    }
}
