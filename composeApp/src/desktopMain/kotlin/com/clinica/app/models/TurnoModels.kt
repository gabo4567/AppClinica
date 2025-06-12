package com.clinica.app.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class TurnoDTO(
    val id: Long?,
    val comprobante: String,
    val idPaciente: Long,
    val idProfesional: Long,
    @Serializable(with = LocalDateTimeSerializer::class)
    val fechaHora: LocalDateTime,
    val duracion: Int,
    val idEstado: Long,
    val observaciones: String? = null
)
