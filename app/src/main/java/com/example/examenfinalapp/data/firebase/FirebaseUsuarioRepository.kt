package com.example.examenfinalapp.data.firebase

import com.example.examenfinalapp.data.UsuarioRepository
import com.example.examenfinalapp.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseUsuarioRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : UsuarioRepository {

    private val col = "usuarios"

    override suspend fun crearOActualizar(usuario: Usuario): Result<Unit> = runCatching {
        require(usuario.uid.isNotBlank()) { "uid vacío" }
        db.collection(col).document(usuario.uid).set(usuario.toMap()).await()
    }

    override suspend fun crearPerfilSiNoExiste(uid: String): Result<Unit> = runCatching {
        val doc = db.collection(col).document(uid).get().await()
        if (!doc.exists()) {
            val nuevo = Usuario(uid = uid, rol = "estudiante")
            db.collection(col).document(uid).set(nuevo.toMap()).await()
        }
    }

    override fun observarUsuario(uid: String): Flow<Usuario?> = callbackFlow {
        val reg = db.collection(col).document(uid).addSnapshotListener { snap, _ ->
            val model = snap?.data?.let { Usuario.fromMap(it) }?.copy(uid = uid)
            trySend(model)
        }
        awaitClose { reg.remove() }
    }
}
