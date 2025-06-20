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

    suspend fun crearProfesional(dto: ProfesionalDTO): ProfesionalDTO {
        // Asegurar que los valores obligatorios estén fijos
        val dtoConValoresFijos = dto.copy(
            idRol = 3L,           // rol fijo para profesional
            idEstado = 1L         // estado activo por defecto
        )

        val response = client.post("http://localhost:8080/api/profesionales") {
            contentType(ContentType.Application.Json)
            setBody(dtoConValoresFijos)
        }

        if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
            return response.body()
        } else {
            throw Exception("Error al crear profesional: ${response.status}")
        }
    }

    suspend fun actualizarProfesional(id: Long, dto: ProfesionalDTO): ProfesionalDTO {
        // Si el estado que viene es 0 o nulo, lo fijamos como activo (1)
        val idEstadoFinal = if (dto.idEstado == 0L) 1L else dto.idEstado

        val dtoConValoresFijos = dto.copy(
            idRol = 3L,
            idEstado = idEstadoFinal
        )

        println("Actualizando profesional ID: $id con idEstado: $idEstadoFinal")

        val response = client.put("http://localhost:8080/api/profesionales/$id") {
            contentType(ContentType.Application.Json)
            setBody(dtoConValoresFijos)
        }

        if (response.status == HttpStatusCode.OK) {
            return response.body()
        } else {
            throw Exception("Error al actualizar profesional: ${response.status}")
        }
    }


    suspend fun eliminarProfesional(id: Long) {
        val dto = mapOf("idEstado" to 2)
        val response = client.put("http://localhost:8080/api/profesionales/$id") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }

        if (!response.status.isSuccess()) {
            throw Exception("Error al cambiar estado del profesional: ${response.status}")
        }
    }



}
