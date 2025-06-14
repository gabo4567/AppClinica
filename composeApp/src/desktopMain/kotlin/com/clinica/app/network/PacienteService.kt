package com.clinica.app.network

import RegistroPacienteDTO
import com.clinica.app.models.Paciente
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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

    suspend fun actualizarPaciente(id: Long, dto: RegistroPacienteDTO): Paciente {
        // Asignar los valores fijos que faltan en el DTO antes de enviarlo
        val dtoConValoresFijos = dto.copy(
            idRol = 4L,             // rol paciente fijo
            idEstadoPersona = 1L,   // estado persona activo fijo
            idEstadoPaciente = 1L   // estado paciente activo fijo
        )

        val jsonString = Json { encodeDefaults = true }.encodeToString(dtoConValoresFijos)
        println("📤 JSON enviado al backend:")
        println(jsonString)

        val response: HttpResponse = client.put("http://localhost:8080/api/pacientes/$id") {
            contentType(ContentType.Application.Json)
            setBody(jsonString)
        }

        if (response.status == HttpStatusCode.OK) {
            return response.body()
        } else {
            throw Exception("Error al actualizar paciente: ${response.status}")
        }
    }


    suspend fun eliminarPaciente(id: Long) {
        println(">>> Enviando solicitud para eliminar paciente con ID: $id")
        val response: HttpResponse = client.delete("http://localhost:8080/api/pacientes/$id")
        if (response.status != HttpStatusCode.NoContent) {
            throw Exception("Error al eliminar paciente: ${response.status}")
        }
    }


}
