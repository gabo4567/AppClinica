package com.clinica.app.network

import com.clinica.app.models.LoginRequestDTO
import com.clinica.app.models.LoginResponseDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object LoginService {

    private const val BASE_URL = "http://localhost:8080/api/login" // Cambiar si usás otro host o puerto

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun loginSecretaria(email: String, contrasenia: String): LoginResponseDTO {
        val request = LoginRequestDTO(email, contrasenia)

        return client.post("$BASE_URL/secretaria") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
