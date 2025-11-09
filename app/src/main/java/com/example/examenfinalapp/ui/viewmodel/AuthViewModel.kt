package com.example.examenfinalapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examenfinalapp.data.firebase.FirebaseAuthRepository
import com.example.examenfinalapp.data.firebase.FirebaseUsuarioRepository
import com.example.examenfinalapp.model.RolUsuario
import com.example.examenfinalapp.model.Usuario
import kotlinx.coroutines.launch

data class AuthUiState(
    val loading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val authRepo: FirebaseAuthRepository,
    private val usuarioRepo: FirebaseUsuarioRepository
) : ViewModel() {

    var uiState = androidx.compose.runtime.mutableStateOf(AuthUiState())
        private set

    fun signUpYCrearPerfil(
        email: String,
        password: String,
        nombre: String,
        carnet: String,
        carrera: String,
        rol: RolUsuario
    ) {
        uiState.value = AuthUiState(loading = true)
        viewModelScope.launch {
            val resAuth = authRepo.signUp(email, password)
            val uid = resAuth.getOrNull()
            if (uid == null) {
                uiState.value = AuthUiState(error = resAuth.exceptionOrNull()?.message)
                return@launch
            }

            val usuario = Usuario(
                uid = uid,
                nombre = nombre,
                carnet = carnet,
                carrera = carrera,
                email = email,
                rol = rol
            )
            val resPerfil = usuarioRepo.crearOActualizar(usuario)
            if (resPerfil.isFailure) {
                uiState.value = AuthUiState(error = resPerfil.exceptionOrNull()?.message)
                return@launch
            }

            uiState.value = AuthUiState()
        }
    }

    fun signInYAsegurarPerfil(email: String, password: String) {
        uiState.value = AuthUiState(loading = true)
        viewModelScope.launch {
            val res = authRepo.signIn(email, password)
            if (res.isFailure) {
                uiState.value = AuthUiState(error = res.exceptionOrNull()?.message)
            } else {
                uiState.value = AuthUiState()
            }
        }
    }
}
