package com.clinica.app.network

import ProfesionalDTO
import com.clinica.app.models.Paciente
import com.clinica.app.models.TurnoDTO
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

object TurnoApi {

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    // Función para parsear JSON a TurnoDTO
    fun fromJson(jsonString: String): TurnoDTO =
        json.decodeFromString(jsonString)

    // Función para convertir TurnoDTO a JSON String
    fun toJson(turno: TurnoDTO): String =
        json.encodeToString(turno)

    suspend fun obtenerTodosLosTurnos(): List<TurnoDTO> {
        val client = KtorClientConfig.config
        val response = client.get("http://localhost:8080/api/turnos") // Cambia la URL si usás otra
        return response.body()
    }

    suspend fun cancelarTurno(turno: TurnoDTO): Boolean {
        val client = KtorClientConfig.config
        // Crear una copia del turno con idEstado = 10 (Cancelado)
        val turnoCancelado = turno.copy(idEstado = 10)

        val response: HttpResponse = client.put("http://localhost:8080/api/turnos/${turno.id}") {
            contentType(ContentType.Application.Json)
            setBody(turnoCancelado)  // enviamos el JSON con el turno actualizado
        }
        return response.status.isSuccess()
    }

    suspend fun obtenerTodosLosPacientes(): List<Paciente> {
        val client = KtorClientConfig.config
        val response = client.get("http://localhost:8080/api/pacientes")
        return response.body()
    }

    suspend fun obtenerTodosLosProfesionales(): List<ProfesionalDTO> {
        val client = KtorClientConfig.config
        val response = client.get("http://localhost:8080/api/profesionales")
        return response.body()
    }

}
