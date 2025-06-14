package com.clinica.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable

@Composable
fun EspecialidadScreen() {
    val especialidades = listOf(
        EspecialidadData("Clínica General", "Atención integral para todas las edades, con diagnóstico y tratamiento de enfermedades comunes."),
        EspecialidadData("Pediatría", "Cuidado de la salud de bebés, niños y adolescentes."),
        EspecialidadData("Ginecología", "Atención de la salud femenina, controles, embarazo y enfermedades específicas."),
        EspecialidadData("Cardiología", "Diagnóstico y tratamiento de enfermedades del corazón y sistema circulatorio.")
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Banner superior con título
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)) // Azul oscuro
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Especialidades de la Clínica Salud Total",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        especialidades.forEach { especialidad ->
            EspecialidadCard(especialidad)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

data class EspecialidadData(val nombre: String, val descripcion: String)

@Composable
fun EspecialidadCard(especialidad: EspecialidadData) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(0.8f), // ancho 80% y centrado en la columna padre
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = especialidad.nombre,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { expanded = !expanded }) {
                    // Icono de + o - para desplegar (puede ser texto si no tenés iconos)
                    Text(if (expanded) "-" else "+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }

            AnimatedVisibility(visible = expanded) {
                Text(
                    text = especialidad.descripcion,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

