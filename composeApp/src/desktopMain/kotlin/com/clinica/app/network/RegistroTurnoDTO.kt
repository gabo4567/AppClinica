package com.clinica.app.network

import kotlinx.serialization.Serializable

@Serializable
data class RegistroTurnoDTO(
    val idPaciente: Long,
    val idProfesional: Long,
    // @Serializable(with = LocalDateTimeSerializer::class)
    // val fechaHora: LocalDateTime,
    val fechaHora: String,
    val duracion: Int,
    val idEstado: Long = 10, // Estado "programado"
    val observaciones: String? = null
)
