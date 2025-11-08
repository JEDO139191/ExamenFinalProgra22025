package com.example.examenfinalapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examenfinalapp.data.PrestamosRepository
import com.example.examenfinalapp.model.Prestamo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AdminUiState(
    val pendientes: List<Prestamo> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null,
    val toast: String? = null
)

class AdminViewModel(private val repo: PrestamosRepository) : ViewModel() {
    private val _ui = MutableStateFlow(AdminUiState())
    val ui: StateFlow<AdminUiState> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            repo.observarPendientes()
                .onEach { _ui.value = _ui.value.copy(pendientes = it, loading = false) }
                .catch { _ui.value = _ui.value.copy(loading = false, error = it.message) }
                .collect()
        }
    }

    fun aprobar(id: String) = op { repo.aprobarPrestamo(id) }
    fun rechazar(id: String, motivo: String?) = op { repo.rechazarPrestamo(id, motivo) }
    fun devuelto(id: String) = op { repo.marcarDevuelto(id) }
    private fun op(block: suspend () -> Result<Unit>) = viewModelScope.launch {
        val r = block()
        _ui.value = _ui.value.copy(toast = r.exceptionOrNull()?.message ?: "Operación realizada")
    }
    fun clearToast() { _ui.value = _ui.value.copy(toast = null) }
}
