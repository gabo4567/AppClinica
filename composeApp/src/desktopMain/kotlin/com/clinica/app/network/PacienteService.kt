package com.clinica.app.network

import com.clinica.app.models.Paciente
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

object PacienteService {
    private val client = KtorClientConfig.config

    suspend fun getPacientes(): List<Paciente> {
        val response: HttpResponse = client.get("http://localhost:8080/api/pacientes")
        if (response.status == HttpStatusCode.OK) {
            return response.body()
        } else {
            throw Exception("Error al obtener pacientes: ${response.status}")
        }
    }
}
