package com.clinica.app.network

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

    suspend fun crearTurno(dto: RegistroTurnoDTO): Boolean {
        val client = KtorClientConfig.config

        val response: HttpResponse = client.post("http://localhost:8080/api/turnos") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }

        return response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK
    }

    suspend fun actualizarTurno(id: Long, dto: RegistroTurnoDTO): Boolean {
        println("DEBUG - Actualizando turno con ID: $id y DTO: $dto")
        val client = KtorClientConfig.config

        val response: HttpResponse = client.put("http://localhost:8080/api/turnos/$id") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }

        println("DEBUG - Respuesta status: ${response.status}")

        return response.status == HttpStatusCode.OK
    }

    suspend fun cancelarTurno(turno: TurnoDTO): Boolean {
        val client = KtorClientConfig.config
        val turnoCancelado = turno.copy(idEstado = 11)

        val response: HttpResponse = client.put("http://localhost:8080/api/turnos/${turno.id}") {
            contentType(ContentType.Application.Json)
            setBody(turnoCancelado)  // enviamos el JSON con el turno actualizado
        }
        return response.status.isSuccess()
    }


}
