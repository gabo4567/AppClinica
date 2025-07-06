package com.clinica.app.models

import kotlinx.serialization.Serializable

@Serializable
data class TurnosPorEstadoDTO(
    val especialidad: String,
    val totalTurnos: Long,
    val cancelados: Long,
    val reprogramados: Long,
    val atendidos: Long
)
