package com.clinica.app.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDTO(
    val email: String,
    val contrasenia: String
)

@Serializable
data class LoginResponseDTO(
    val message: String
)
