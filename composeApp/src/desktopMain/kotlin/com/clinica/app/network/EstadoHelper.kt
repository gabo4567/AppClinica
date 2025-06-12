package com.clinica.app.network

fun nombreEstadoPorId(idEstado: Long?): String {
    return when (idEstado) {
        9L -> "Confirmado"
        10L -> "Programado"
        11L -> "Cancelado"
        12L -> "Reprogramado"
        13L -> "Atendido"
        else -> "Desconocido"
    }
}
