package com.example.examenfinalapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examenfinalapp.data.AuthRepository
import com.example.examenfinalapp.data.UsuarioRepository
import com.example.examenfinalapp.model.Usuario
import kotlinx.coroutines.launch

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class AuthViewModel(
    private val authRepo: AuthRepository,
    private val userRepo: UsuarioRepository
) : ViewModel() {

    var uiState: AuthUiState = AuthUiState()
        private set

    /** Registro + creación automática del documento /usuarios/{uid} */
    fun signUpYCrearPerfil(
        email: String,
        password: String,
        nombre: String,
        carnet: String,
        carrera: String
    ) = viewModelScope.launch {
        uiState = uiState.copy(loading = true, error = null)
        try {
            val uid = authRepo.signUp(email, password).getOrThrow()
            val usuario = Usuario(
                uid = uid,
                rol = "estudiante",
                nombreCompleto = nombre,
                carnet = carnet,
                carrera = carrera,
                fotoUrl = ""
            )
            userRepo.crearOActualizar(usuario).getOrThrow()
            uiState = uiState.copy(loading = false, success = true)
        } catch (e: Exception) {
            uiState = uiState.copy(loading = false, error = e.message)
        }
    }

    /** Login + si no existe el documento, lo crea */
    fun signInYAsegurarPerfil(email: String, password: String) = viewModelScope.launch {
        uiState = uiState.copy(loading = true, error = null)
        try {
            authRepo.signIn(email, password).getOrThrow()
            val uid = authRepo.uidActual ?: error("UID nulo tras login")
            userRepo.crearPerfilSiNoExiste(uid).getOrThrow()
            uiState = uiState.copy(loading = false, success = true)
        } catch (e: Exception) {
            uiState = uiState.copy(loading = false, error = e.message)
        }
    }
}
