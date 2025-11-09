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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.examenfinalapp.ui.theme.ExamenFinalAppTheme

// Screens
import com.example.examenfinalapp.ui.screens.LoginScreen
import com.example.examenfinalapp.ui.screens.RegistroScreen
import com.example.examenfinalapp.ui.screens.CatalogoScreen
import com.example.examenfinalapp.ui.screens.MisPrestamosScreen
import com.example.examenfinalapp.ui.screens.AdminSolicitudesScreen

// ViewModels
import com.example.examenfinalapp.ui.viewmodel.AuthViewModel
import com.example.examenfinalapp.ui.viewmodel.SessionViewModel
import com.example.examenfinalapp.ui.viewmodel.CatalogoViewModel
import com.example.examenfinalapp.ui.viewmodel.MisPrestamosViewModel
import com.example.examenfinalapp.ui.viewmodel.AdminViewModel

// Repos
import com.example.examenfinalapp.data.firebase.FirebaseAuthRepository
import com.example.examenfinalapp.data.firebase.FirebaseUsuarioRepository
import com.example.examenfinalapp.data.firebase.FirebaseEquiposRepository
import com.example.examenfinalapp.data.firebase.FirebasePrestamosRepository
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExamenFinalAppTheme {

                // ===================== 🔹 Repositorios 🔹 =====================
                val authRepo = remember { FirebaseAuthRepository() }
                val userRepo = remember { FirebaseUsuarioRepository() }
                val equiposRepo = remember { FirebaseEquiposRepository() }
                val prestamosRepo = remember { FirebasePrestamosRepository() }

                // ===================== 🔹 ViewModels 🔹 =====================
                val authVm = remember { AuthViewModel(authRepo, userRepo) }
                val sessionVm = remember { SessionViewModel(authRepo, userRepo) }
                val catalogoVm = remember { CatalogoViewModel(equiposRepo, prestamosRepo) }
                val adminVm = remember { AdminViewModel(prestamosRepo) }

                // ===================== 🔹 Estado de Sesión 🔹 =====================
                LaunchedEffect(Unit) { sessionVm.loadForCurrentUser() }
                val sessionState by sessionVm.state.collectAsState()

                val misVm = remember(sessionState.uid) {
                    sessionState.uid?.let { MisPrestamosViewModel(it, prestamosRepo) }
                }

                // ===================== 🔹 Navegación 🔹 =====================
                val nav = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

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
                                if (sessionState.uid != null) {
                                    if (sessionState.isAdmin) {
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
                ) { innerPadding ->

                    // Destino inicial (solo para el primer render)
                    val startDest = when {
                        sessionState.uid == null -> Routes.Login
                        sessionState.isAdmin -> Routes.AdminSolicitudes
                        else -> Routes.Catalogo
                    }

                    NavHost(
                        navController = nav,
                        startDestination = startDest,
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        // ===================== 🔹 Login 🔹 =====================
                        composable(Routes.Login) {
                            LoginScreen(
                                vm = authVm,
                                onOk = {
                                    sessionVm.loadForCurrentUser()
                                    val uid = authRepo.uidActual ?: return@LoginScreen
                                    scope.launch {
                                        val usuario = userRepo.getUsuario(uid).getOrNull()
                                        if (usuario?.esAdmin == true) {
                                            nav.navigate(Routes.AdminSolicitudes) {
                                                popUpTo(Routes.Login) { inclusive = true }
                                            }
                                        } else {
                                            nav.navigate(Routes.Catalogo) {
                                                popUpTo(Routes.Login) { inclusive = true }
                                            }
                                        }
                                    }
                                },
                                onGoRegistro = { nav.navigate(Routes.Registro) }
                            )
                        }

                        // ===================== 🔹 Registro 🔹 =====================
                        composable(Routes.Registro) {
                            RegistroScreen(
                                vm = authVm,
                                onOk = {
                                    sessionVm.loadForCurrentUser()
                                    val uid = authRepo.uidActual ?: return@RegistroScreen
                                    scope.launch {
                                        val usuario = userRepo.getUsuario(uid).getOrNull()
                                        if (usuario?.esAdmin == true) {
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
                            )
                        }

                        // ===================== 🔹 Catálogo 🔹 =====================
                        composable(Routes.Catalogo) {
                            CatalogoScreen(
                                vm = catalogoVm,
                                snackbarHostState = snackbarHostState
                            )
                        }

                        // ===================== 🔹 Mis préstamos 🔹 =====================
                        composable(Routes.MisPrestamos) {
                            misVm?.let {
                                MisPrestamosScreen(vm = it)
                            } ?: Text("Debes iniciar sesión.")
                        }

                        // ===================== 🔹 Admin Solicitudes 🔹 =====================
                        composable(Routes.AdminSolicitudes) {
                            if (sessionState.isAdmin) {
                                AdminSolicitudesScreen(
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
        }
    }
}

// ===================== 🔹 Rutas Centralizadas 🔹 =====================
object Routes {
    const val Login = "login"
    const val Registro = "registro"
    const val Catalogo = "catalogo"
    const val MisPrestamos = "mis_prestamos"
    const val AdminSolicitudes = "admin_solicitudes"
}
