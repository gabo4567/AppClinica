package com.clinica.app.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// DTO para mapear respuesta
@Serializable
data class HorarioDisponibleDTO(
    val fecha: String,
    val horarios: List<String>
)

object HorarioDisponibleApi {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    // URL base de tu API (ajustar)
    private const val BASE_URL = "http://localhost:8080/api"

    // Obtener lista de fechas disponibles para un profesional
    suspend fun obtenerFechasDisponibles(idProfesional: Long): List<String> {
        val listaDto: List<HorarioDisponibleDTO> = client.get("$BASE_URL/horarios-disponibles/$idProfesional").body()
        return listaDto.map { it.fecha }
    }

    // Obtener horarios disponibles para un profesional y una fecha específica
    suspend fun obtenerHorariosDisponibles(idProfesional: Long, fecha: String): List<String> {
        val listaDto: List<HorarioDisponibleDTO> = client.get("$BASE_URL/horarios-disponibles/$idProfesional").body()
        return listaDto.firstOrNull { it.fecha == fecha }?.horarios ?: emptyList()
    }
}
