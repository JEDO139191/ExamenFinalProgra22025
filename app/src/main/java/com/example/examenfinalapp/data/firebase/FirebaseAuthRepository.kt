package com.example.examenfinalapp.data

import com.example.examenfinalapp.model.Usuario
import kotlinx.coroutines.flow.Flow

interface UsuarioRepository {
    suspend fun crearOActualizar(usuario: Usuario): Result<Unit>
    suspend fun crearPerfilSiNoExiste(uid: String): Result<Unit>
    fun observarUsuario(uid: String): Flow<Usuario?>
}
