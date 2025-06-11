package com.clinica.app

import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.clinica.app.ui.LoginScreen
import com.clinica.app.ui.MainScreen

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "AppClinica",
    ) {
        var isLoggedIn by remember { mutableStateOf(false) }

        if (!isLoggedIn) {
            LoginScreen(onLoginSuccess = {
                isLoggedIn = true
            })
        } else {
            MainScreen(onLogout = {
                isLoggedIn = false
            })
        }
    }
}
