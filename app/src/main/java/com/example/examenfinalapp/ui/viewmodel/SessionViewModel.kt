package com.example.examenfinalapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examenfinalapp.data.firebase.FirebaseAuthRepository
import com.example.examenfinalapp.data.firebase.FirebaseUsuarioRepository
import com.example.examenfinalapp.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SessionState(
    val loading: Boolean = false,
    val uid: String? = null,
    val usuario: Usuario? = null,
    val isAdmin: Boolean = false,
    val error: String? = null
)

class SessionViewModel(
    private val authRepo: FirebaseAuthRepository,
    private val usuarioRepo: FirebaseUsuarioRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SessionState())
    val state: StateFlow<SessionState> = _state

    /**
     * Carga la sesión actual del usuario autenticado en Firebase.
     * Si existe un UID, obtiene los datos desde Firestore.
     */
    fun loadForCurrentUser() {
        val uid = authRepo.uidActual ?: run {
            _state.value = SessionState(loading = false, uid = null)
            return
        }

        _state.value = _state.value.copy(loading = true, uid = uid, error = null)

        viewModelScope.launch {
            try {
                val result = usuarioRepo.getUsuario(uid)
                val user = result.getOrNull()

                _state.value = _state.value.copy(
                    usuario = user,
                    isAdmin = user?.esAdmin ?: false,
                    loading = false,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = e.message ?: "Error al cargar usuario"
                )
            }
        }
    }

    /**
     * Cierra la sesión actual.
     */
    fun signOut() {
        authRepo.signOut()
        _state.value = SessionState()
    }
}
