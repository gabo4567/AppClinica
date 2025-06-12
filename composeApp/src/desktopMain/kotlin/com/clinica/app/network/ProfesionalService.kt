package com.clinica.app.network

import ProfesionalDTO
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

object ProfesionalService {
    private val client = KtorClientConfig.config

    suspend fun getProfesionales(): List<ProfesionalDTO> {
        val response = client.get("http://localhost:8080/api/profesionales")
        if (response.status == HttpStatusCode.OK) {
            return response.body()
        } else {
            throw Exception("Error al obtener profesionales: ${response.status}")
        }
    }
}
