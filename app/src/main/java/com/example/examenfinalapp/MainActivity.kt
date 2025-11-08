package com.example.examenfinalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.examenfinalapp.ui.theme.ExamenFinalAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExamenFinalAppTheme {
                val nav = rememberNavController()
                AppScaffold(nav)
            }
        }
    }
}

/** Rutas centrales del proyecto */
object Routes {
    const val Login = "login"
    const val Registro = "registro"
    const val Catalogo = "catalogo"            // Estudiante
    const val MisPrestamos = "mis_prestamos"   // Estudiante
    const val AdminSolicitudes = "admin_solicitudes" // Admin
}

/** Estructura base con AppBar y NavHost */
@Composable
private fun AppScaffold(nav: NavHostController) {
    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: Routes.Login

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = {
                Text(
                    when (currentRoute) {
                        Routes.Login -> "Iniciar sesión"
                        Routes.Registro -> "Registro"
                        Routes.Catalogo -> "Catálogo de equipos"
                        Routes.MisPrestamos -> "Mis préstamos"
                        Routes.AdminSolicitudes -> "Solicitudes (Admin)"
                        else -> "Laboratorio"
                    }
                )
            })
        }
    ) { _ ->
        AppNavHost(nav)
    }
}

/** Navegación mínima: pantallas placeholder que iremos sustituyendo */
@Composable
private fun AppNavHost(nav: NavHostController) {
    NavHost(navController = nav, startDestination = Routes.Login) {

        composable(Routes.Login) {
            ScreenText(
                "Pantalla Login (aquí irá Auth). " +
                        "Botón → ir a Registro o redirigir según rol."
            )
        }

        composable(Routes.Registro) {
            ScreenText(
                "Pantalla Registro Estudiante (perfil + email/password)."
            )
        }

        // Estudiante
        composable(Routes.Catalogo) {
            ScreenText(
                "Catálogo de equipos: listar, ver disponibilidad y 'Solicitar préstamo'."
            )
        }
        composable(Routes.MisPrestamos) {
            ScreenText("Mis préstamos: Pendiente/Aprobado/Rechazado/Devuelto.")
        }

        // Admin
        composable(Routes.AdminSolicitudes) {
            ScreenText(
                "Panel Admin: Aprobar/Rechazar solicitudes y Marcar Devuelto."
            )
        }
    }
}

/** Placeholder temporal: lo vamos a reemplazar por UI real */
@Composable
private fun ScreenText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge
    )
}
