package com.clinica.app.models

import kotlinx.serialization.Serializable

@Serializable
data class ResultadoTurnosPorDiaDTO(
    val turnosPorDia: List<CantidadTurnosPorDiaDTO>,
    val totalTurnos: Long
)
