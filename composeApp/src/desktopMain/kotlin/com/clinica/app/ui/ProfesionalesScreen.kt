package com.clinica.app.ui

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

@Composable
fun ProfesionalesScreen() {
    var estadoFiltro by remember { mutableStateOf<Long?>(null) } // null = todos, 1L = activo, 2L = inactivo
    var mostrarFormularioAgregar by remember { mutableStateOf(false) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }
    var eliminarErrorMessage by remember { mutableStateOf<String?>(null) }

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

                Spacer(modifier = Modifier.width(8.dp))

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

                Spacer(modifier = Modifier.width(8.dp))

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

            // Aquí luego irá la lista de profesionales, pero por ahora solo el encabezado

            ProfesionalesHeader()

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

        // Mensaje éxito centrado (igual que en pacientes)
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
                        containerColor = Color(0xFF4CAF50) // verde éxito
                    ) {
                        Text(mensaje, color = Color.White)
                    }
                }
            }
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
                modifier = Modifier.padding(start = 25.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                "Nombre",
                modifier = Modifier.padding(start = 60.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
            Text(
                "Apellido",
                modifier = Modifier.padding(start = 55.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
            Text(
                "Email",
                modifier = Modifier.padding(start = 125.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
            Text(
                "Teléfono",
                modifier = Modifier.padding(start = 180.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Text(
                "Especialidad",
                modifier = Modifier.padding(start = 140.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
            Text(
                "Fecha Nacimiento",
                modifier = Modifier.padding(start = 110.dp),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(0.dp)) // espacio para botones
        }
    }
}
