package com.clinica.app.network

import com.clinica.app.models.LocalDateTimeSerializer
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class RegistroTurnoDTO(
    val idPaciente: Long,
    val idProfesional: Long,
    @Serializable(with = LocalDateTimeSerializer::class)
    val fechaHora: LocalDateTime,
    val duracion: Int,
    val idEstado: Long = 1, // Estado "activo" o "reservado"
    val observaciones: String? = null
)
