package com.clinica.app.models

import kotlinx.serialization.Serializable

@Serializable
data class CantidadTurnosPorProfesionalDTO(
    val idProfesional: Long,
    val nombreProfesional: String,
    val cantidadTurnos: Long
)
