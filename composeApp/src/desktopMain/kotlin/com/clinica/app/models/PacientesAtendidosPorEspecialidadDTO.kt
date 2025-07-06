package com.clinica.app.models

import kotlinx.serialization.Serializable

@Serializable
data class PacientesAtendidosPorEspecialidadDTO(
    val especialidad: String,
    val cantidadPacientes: Long
)
