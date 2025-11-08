package com.example.examenfinalapp.data

import com.example.examenfinalapp.model.Equipo
import kotlinx.coroutines.flow.Flow

interface EquiposRepository {
    fun observarEquiposActivos(): Flow<List<Equipo>>
    suspend fun crearEquipo(equipo: Equipo): Result<Unit>      // Admin
    suspend fun actualizarEquipo(equipo: Equipo): Result<Unit> // Admin
}
