package com.example.examenfinalapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examenfinalapp.data.EquiposRepository
import com.example.examenfinalapp.data.PrestamosRepository
import com.example.examenfinalapp.model.Equipo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CatalogoUiState(
    val equipos: List<Equipo> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val toast: String? = null
)

class CatalogoViewModel(
    private val equiposRepo: EquiposRepository,
    private val prestamosRepo: PrestamosRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(CatalogoUiState(loading = true))
    val ui: StateFlow<CatalogoUiState> = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            equiposRepo.observarEquiposActivos()
                .onEach { _ui.value = _ui.value.copy(equipos = it, loading = false, error = null) }
                .catch { _ui.value = _ui.value.copy(loading = false, error = it.message) }
                .collect()
        }
    }

    fun solicitar(equipoId: String) = viewModelScope.launch {
        _ui.value = _ui.value.copy(toast = null)
        val r = prestamosRepo.solicitarPrestamo(equipoId)
        _ui.value = _ui.value.copy(toast = r.exceptionOrNull()?.message ?: "Solicitud enviada")
    }

    fun clearToast() { _ui.value = _ui.value.copy(toast = null) }
}
