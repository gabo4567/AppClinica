package com.clinica.app.models

import kotlinx.serialization.Serializable

@Serializable
data class Persona(
    val id: Int,
    val dni: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    val contrasenia: String,
    val idRol: Int,
    val idEspecialidad: Int? = null,
    val idEstado: Int,
    val fechaNacimiento: String
)

@Serializable
data class Paciente(
    val id: Int,
    val persona: Persona,
    val obraSocial: String,
    val idEstado: Int
)
