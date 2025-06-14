package com.clinica.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun InicioScreen() {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        visible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 4.dp),
        verticalArrangement = Arrangement.Top  // Cambiado a Top para que el contenido esté arriba
    ) {

        // Banner superior con elevación y animación
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -40 })
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color(0xFF1565C0), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Clínica Salud Total",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp)) // Un poco de espacio entre banner y contenido

        // Contenido principal con animación
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -20 })
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "¡Bienvenida, secretaria!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "En esta aplicación podrás gestionar pacientes, profesionales, turnos, reportes y consultas",
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Especialidades disponibles:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally  // Esto centra los banners en la pantalla
                ) {
                    EspecialidadBanner(nombre = "Clínica general")
                    EspecialidadBanner(nombre = "Pediatría")
                    EspecialidadBanner(nombre = "Ginecología")
                    EspecialidadBanner(nombre = "Cardiología")
                }

            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Este Spacer empuja el banner inferior hacia abajo

        // Banner inferior con elevación y animación
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.padding(start = 100.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Teléfono: (03722) 444-555", fontSize = 14.sp, color = Color.Black)
                        Text("Dirección: Av. Salud 1234", fontSize = 14.sp, color = Color.Black)
                        Text("Ciudad: Goya, Corrientes", fontSize = 14.sp, color = Color.Black)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        "Horarios de atención: 7:30 - 12:30",
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(end = 200.dp)
                    )
                }

            }
        }
    }
}

@Composable
fun EspecialidadBanner(nombre: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.7f)  // 70% ancho del total para que quede centrado y no muy ancho
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFBBDEFB)) // Azul claro suave
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()    // IMPORTANTE para que el contenido ocupe todo el ancho del banner
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center  // Centra el contenido dentro del Box
        ) {
            Text(
                text = nombre,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF0D47A1),
                textAlign = TextAlign.Center,  // Centrar el texto
                modifier = Modifier.fillMaxWidth()  // para que el texto ocupe todo el ancho y se centre
            )
        }
    }
}

