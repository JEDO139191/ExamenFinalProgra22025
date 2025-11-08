package com.example.examenfinalapp.data

import com.example.examenfinalapp.model.Prestamo
import kotlinx.coroutines.flow.Flow

interface PrestamosRepository {
    fun observarPrestamosUsuario(uid: String): Flow<List<Prestamo>>
    fun observarPendientes(): Flow<List<Prestamo>> // Admin
    suspend fun solicitarPrestamo(equipoId: String): Result<Unit>
    suspend fun aprobarPrestamo(prestamoId: String): Result<Unit>          // Admin
    suspend fun rechazarPrestamo(prestamoId: String, motivo: String?): Result<Unit> // Admin o auto-cancel
    suspend fun marcarDevuelto(prestamoId: String): Result<Unit>           // Admin
}
