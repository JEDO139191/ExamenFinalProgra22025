package com.example.examenfinalapp.data.firebase

import com.example.examenfinalapp.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseUsuarioRepository {

    private val db = FirebaseFirestore.getInstance()
    private val col = "usuarios"

    suspend fun crearOActualizar(usuario: Usuario): Result<Unit> = runCatching {
        db.collection(col).document(usuario.uid).set(usuario.toMap()).await()
    }

    suspend fun getUsuario(uid: String): Result<Usuario?> = runCatching {
        val snap = db.collection(col).document(uid).get().await()
        if (!snap.exists()) return@runCatching null
        Usuario.fromMap(snap.data!!).copy(uid = uid)
    }
}
