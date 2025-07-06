package com.clinica.app.network

import com.clinica.app.models.CantidadTurnosPorDiaDTO
import com.clinica.app.models.CantidadTurnosPorProfesionalDTO
import com.clinica.app.models.PacientesAtendidosPorEspecialidadDTO
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import kotlinx.serialization.json.Json

object ReporteApi {
    suspend fun obtenerCantidadTurnosPorDia(fechaInicio: LocalDate, fechaFin: LocalDate): List<CantidadTurnosPorDiaDTO> {
        val client = KtorClientConfig.config
        val url = "http://localhost:8080/api/turnos/reportes/turnos-por-dia?fechaInicio=$fechaInicio&fechaFin=$fechaFin"
        val response = client.get(url)
        return response.body()
    }

    suspend fun obtenerCantidadTurnosPorProfesional(
        fechaInicio: LocalDate,
        fechaFin: LocalDate,
        filtroNombre: String = ""
    ): List<CantidadTurnosPorProfesionalDTO> {
        val client = KtorClientConfig.config
        val filtroNombreEncoded = URLEncoder.encode(filtroNombre.lowercase(), StandardCharsets.UTF_8.toString())
        val url = "http://localhost:8080/api/turnos/reportes/turnos-por-profesional" +
                "?fechaInicio=$fechaInicio&fechaFin=$fechaFin&nombreProfesional=$filtroNombreEncoded"
        val response = client.get(url)
        return response.body()
    }

    suspend fun obtenerPacientesAtendidosPorEspecialidad(
        especialidad: String?,
        fechaInicio: LocalDate,
        fechaFin: LocalDate
    ): List<PacientesAtendidosPorEspecialidadDTO> {
        val client = KtorClientConfig.config

        // Armamos los parámetros manualmente
        val baseUrl = "http://localhost:8080/api/turnos/reportes/pacientes-por-especialidad"
        val params = mutableListOf(
            "fechaInicio=$fechaInicio",
            "fechaFin=$fechaFin"
        )
        if (!especialidad.isNullOrBlank()) {
            val especialidadEncoded = URLEncoder.encode(especialidad, StandardCharsets.UTF_8.toString())
            params.add("especialidad=$especialidadEncoded")
        }

        val url = "$baseUrl?${params.joinToString("&")}"

        val responseString = client.get(url).bodyAsText()
        return Json { ignoreUnknownKeys = true }.decodeFromString(responseString)
    }

}
