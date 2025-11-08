package com.example.examenfinalapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examenfinalapp.data.AuthRepository
import com.example.examenfinalapp.data.UsuarioRepository
import com.example.examenfinalapp.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class SessionState(
    val uid: String? = null,
    val usuario: Usuario? = null,
    val isAdmin: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null
)

class SessionViewModel(
    private val authRepo: AuthRepository,
    private val usuarioRepo: UsuarioRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SessionState())
    val state: StateFlow<SessionState> = _state

    /** Llamar después de login/registro o al iniciar la app para hidratar la sesión */
    fun loadForCurrentUser() {
        val uid = authRepo.uidActual
        if (uid == null) {
            _state.value = SessionState(uid = null, usuario = null, isAdmin = false)
            return
        }
        _state.value = _state.value.copy(loading = true, uid = uid, error = null)
        viewModelScope.launch {
            try {
                usuarioRepo.observarUsuario(uid).collectLatest { u ->
                    _state.value = _state.value.copy(
                        usuario = u,
                        isAdmin = u?.esAdmin ?: false,
                        loading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun signOut() {
        authRepo.signOut()
        _state.value = SessionState(uid = null, usuario = null, isAdmin = false)
    }
}
