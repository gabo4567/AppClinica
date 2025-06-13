package com.clinica.app.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDTO(
    val nombreUsuario: String,
    val contrasena: String
)

@Serializable
data class LoginResponseDTO(
    val message: String? = null
)
