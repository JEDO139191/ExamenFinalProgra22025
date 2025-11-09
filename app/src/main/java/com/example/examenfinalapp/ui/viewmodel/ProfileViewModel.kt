package com.example.examenfinalapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examenfinalapp.data.StorageRepository
import com.example.examenfinalapp.data.UsuarioRepository
import com.example.examenfinalapp.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val saving: Boolean = false,
    val error: String? = null,
    val ok: Boolean = false
)

class ProfileViewModel(
    private val uid: String,
    private val storageRepo: StorageRepository,
    private val userRepo: UsuarioRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(ProfileUiState())
    val ui: StateFlow<ProfileUiState> = _ui

    fun uploadPhotoAndSave(fileUri: Uri) = viewModelScope.launch {
        _ui.value = ProfileUiState(saving = true)
        try {
            val url = storageRepo.uploadUserPhoto(uid, fileUri).getOrThrow()
            val u = Usuario(uid = uid, fotoUrl = url) // solo necesitamos uid+foto para merge
            userRepo.crearOActualizar(u).getOrThrow()
            _ui.value = ProfileUiState(ok = true)
        } catch (e: Exception) {
            _ui.value = ProfileUiState(error = e.message)
        }
    }
}
