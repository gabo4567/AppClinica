package com.clinica.app.network

import com.clinica.app.models.TurnoDTO
import io.ktor.client.call.*
import io.ktor.client.plugins.*
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


    suspend fun crearTurno(dto: RegistroTurnoDTO): Result<Boolean> {
        val client = KtorClientConfig.config

        return try {
            val response: HttpResponse = client.post("http://localhost:8080/api/turnos") {
                contentType(ContentType.Application.Json)
                setBody(dto)
            }
            if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK) {
                Result.success(true)
            } else {
                Result.failure(Exception("El horario seleccionado ya fue reservado anteriormente"))
            }
        } catch (e: ClientRequestException) {
            // Aquí capturamos errores HTTP 4xx
            val errorBody = e.response.bodyAsText()
            Result.failure(Exception(errorBody))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun actualizarTurno(id: Long, dto: RegistroTurnoDTO): Result<Boolean> {
        val client = KtorClientConfig.config

        return try {
            val response: HttpResponse = client.put("http://localhost:8080/api/turnos/$id") {
                contentType(ContentType.Application.Json)
                setBody(dto)
            }

            if (response.status == HttpStatusCode.OK) {
                Result.success(true)
            } else {
                Result.failure(Exception("El horario seleccionado ya fue reservado anteriormente"))
            }
        } catch (e: ClientRequestException) {
            val errorBody = e.response.bodyAsText()
            Result.failure(Exception(errorBody))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarEstadoTurno(id: Long, idEstado: Long): Result<Boolean> {
        val client = KtorClientConfig.config

        return try {
            val response: HttpResponse = client.patch("http://localhost:8080/api/turnos/$id/estado") {
                parameter("idEstado", idEstado)
            }

            if (response.status == HttpStatusCode.OK) {
                Result.success(true)
            } else {
                Result.failure(Exception("Error al actualizar estado del turno"))
            }
        } catch (e: ClientRequestException) {
            val errorBody = e.response.bodyAsText()
            Result.failure(Exception(errorBody))
        } catch (e: Exception) {
            Result.failure(e)
        }
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
