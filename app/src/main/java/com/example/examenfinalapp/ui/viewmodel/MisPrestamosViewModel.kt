package com.example.examenfinalapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examenfinalapp.data.PrestamosRepository
import com.example.examenfinalapp.model.Prestamo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MisPrestamosUiState(
    val prestamos: List<Prestamo> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null
)

class MisPrestamosViewModel(
    private val uid: String,
    private val repo: PrestamosRepository
) : ViewModel() {
    private val _ui = MutableStateFlow(MisPrestamosUiState())
    val ui: StateFlow<MisPrestamosUiState> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            repo.observarPrestamosUsuario(uid)
                .onEach { _ui.value = MisPrestamosUiState(prestamos = it, loading = false) }
                .catch { _ui.value = MisPrestamosUiState(loading = false, error = it.message) }
                .collect()
        }
    }
}
