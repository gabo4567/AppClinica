package com.clinica.app.models

import kotlinx.serialization.Serializable

@Serializable
data class Persona(
    val id: Long,
    val dni: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    val idRol: Long,
    val idEspecialidad: Long? = null,
    val idEstado: Long,
    val fechaNacimiento: String
)

@Serializable
data class Paciente(
    val id: Long,
    val persona: Persona,
    val obraSocial: String,
    val idEstado: Long
)
