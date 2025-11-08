package com.example.examenfinalapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.examenfinalapp.ui.theme.ExamenFinalAppTheme

// --------- RUTAS CENTRALES ---------
object Routes {
    const val Login = "login"
    const val Registro = "registro"
    const val Catalogo = "catalogo"               // Estudiante
    const val MisPrestamos = "mis_prestamos"      // Estudiante
    const val AdminSolicitudes = "admin_solicitudes" // Admin
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExamenFinalAppTheme {
                AppRoot()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppRoot() {
    // ==== Repositorios ====
    val authRepo = remember { com.example.examenfinalapp.data.firebase.FirebaseAuthRepository() }
    val userRepo = remember { com.example.examenfinalapp.data.firebase.FirebaseUsuarioRepository() }
    val equiposRepo = remember { com.example.examenfinalapp.data.firebase.FirebaseEquiposRepository() }
    val prestamosRepo = remember { com.example.examenfinalapp.data.firebase.FirebasePrestamosRepository() }

    // ==== ViewModels ====
    val authVm = remember { com.example.examenfinalapp.ui.viewmodel.AuthViewModel(authRepo, userRepo) }
    val sessionVm = remember { com.example.examenfinalapp.ui.viewmodel.SessionViewModel(authRepo, userRepo) }
    val catalogoVm = remember { com.example.examenfinalapp.ui.viewmodel.CatalogoViewModel(equiposRepo, prestamosRepo) }
    val adminVm = remember { com.example.examenfinalapp.ui.viewmodel.AdminViewModel(prestamosRepo) }

    // uid puede ser nulo antes de login
    val uid = authRepo.uidActual
    val misVm = remember(uid) {
        if (uid != null)
            com.example.examenfinalapp.ui.viewmodel.MisPrestamosViewModel(uid, prestamosRepo)
        else null
    }

    // Cargar sesión al abrir app
    LaunchedEffect(Unit) { sessionVm.loadForCurrentUser() }
    val session by sessionVm.state.collectAsState()

    val nav = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            val backStack by nav.currentBackStackEntryAsState()
            val route = backStack?.destination?.route

            TopAppBar(
                title = {
                    Text(
                        when (route) {
                            Routes.Login -> "Iniciar sesión"
                            Routes.Registro -> "Registro"
                            Routes.Catalogo -> "Catálogo de equipos"
                            Routes.MisPrestamos -> "Mis préstamos"
                            Routes.AdminSolicitudes -> "Solicitudes (Admin)"
                            else -> "Laboratorio"
                        }
                    )
                },
                actions = {
                    if (session.uid != null) {
                        if (session.isAdmin) {
                            TextButton(onClick = { nav.navigate(Routes.AdminSolicitudes) }) {
                                Text("Solicitudes")
                            }
                        } else {
                            TextButton(onClick = { nav.navigate(Routes.MisPrestamos) }) {
                                Text("Mis préstamos")
                            }
                        }
                        TextButton(onClick = {
                            sessionVm.signOut()
                            nav.navigate(Routes.Login) {
                                popUpTo(0) { inclusive = true }
                            }
                        }) { Text("Salir") }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        // StartDestination según estado/rol
        NavHost(
            navController = nav,
            startDestination = when {
                session.uid == null -> Routes.Login
                session.isAdmin -> Routes.AdminSolicitudes
                else -> Routes.Catalogo
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            composable(Routes.Login) {
                com.example.examenfinalapp.ui.screens.LoginScreen(
                    vm = authVm,
                    onOk = {
                        // tras login: recarga sesión y redirige
                        sessionVm.loadForCurrentUser()
                        // navegación práctica una vez que uid esté listo
                        LaunchedEffect(session.uid to session.isAdmin) {
                            if (session.uid != null) {
                                if (session.isAdmin) {
                                    nav.navigate(Routes.AdminSolicitudes) {
                                        popUpTo(Routes.Login) { inclusive = true }
                                    }
                                } else {
                                    nav.navigate(Routes.Catalogo) {
                                        popUpTo(Routes.Login) { inclusive = true }
                                    }
                                }
                            }
                        }
                    },
                    onGoRegistro = { nav.navigate(Routes.Registro) }
                )
            }

            composable(Routes.Registro) {
                com.example.examenfinalapp.ui.screens.RegistroScreen(
                    vm = authVm,
                    onOk = {
                        sessionVm.loadForCurrentUser()
                        nav.navigate(Routes.Catalogo) {
                            popUpTo(Routes.Login) { inclusive = true }
                        }
                    }
                )
            }

            // Estudiante
            composable(Routes.Catalogo) {
                com.example.examenfinalapp.ui.screens.CatalogoScreen(
                    vm = catalogoVm,
                    snackbarHostState = snackbarHostState
                )
            }
            composable(Routes.MisPrestamos) {
                misVm?.let {
                    com.example.examenfinalapp.ui.screens.MisPrestamosScreen(vm = it)
                } ?: Text("Debes iniciar sesión.")
            }

            // Admin
            composable(Routes.AdminSolicitudes) {
                if (session.isAdmin) {
                    com.example.examenfinalapp.ui.screens.AdminSolicitudesScreen(
                        vm = adminVm,
                        snackbarHostState = snackbarHostState
                    )
                } else {
                    Text("Acceso solo para administradores.")
                }
            }
        }
    }
}
