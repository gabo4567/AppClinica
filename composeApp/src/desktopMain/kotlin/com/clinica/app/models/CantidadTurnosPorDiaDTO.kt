package com.clinica.app.models

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class CantidadTurnosPorDiaDTO(
    @Serializable(with = LocalDateSerializer::class)
    val fecha: LocalDate,
    val cantidad: Long
)
