package com.clinica.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import com.clinica.app.network.LoginService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    var nombreUsuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD)), // Fondo celeste clarito
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 440.dp)           // más delgado
                .heightIn(min = 500.dp)          // más alto
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(40.dp))  // bordes más suaves
                .background(color = Color.White, shape = RoundedCornerShape(40.dp))
                .padding(70.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Clínica Salud Total",
                color = Color(0xFF1565C0), // azul fuerte
                fontSize = 28.sp,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                "Iniciar sesión - Secretaria",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 25.sp)
            )


            Spacer(modifier = Modifier.height(25.dp))

            OutlinedTextField(
                value = nombreUsuario,
                onValueChange = { nombreUsuario = it },
                label = { Text("Nombre de usuario") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                textStyle = LocalTextStyle.current.copy(fontSize = 20.sp)
            )

            Spacer(modifier = Modifier.height(25.dp))

            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = LoginService.loginSecretaria(nombreUsuario, contrasena)
                            onLoginSuccess(response.message ?: "Login exitoso")
                        } catch (e: Exception) {
                            errorMessage = "Login fallido: ${e.message}"
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.width(200.dp)
            ) {
                Text("Iniciar sesión",
                    style = LocalTextStyle.current.copy(fontSize = 17.sp))
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
