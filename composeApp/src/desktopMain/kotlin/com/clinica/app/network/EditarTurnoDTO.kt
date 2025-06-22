package com.clinica.app.network

import kotlinx.serialization.Serializable

@Serializable
data class EditarTurnoDTO(
    val id: Long,
    val idPaciente: Long,
    val idProfesional: Long,
    val fechaHora: String,       // Formato ISO: "2025-06-25T10:00"
    val duracion: Int,
    val idEstado: Long = 10,
    val observaciones: String? = null
)