package com.example.examenfinalapp.data.firebase

import com.example.examenfinalapp.data.EquiposRepository
import com.example.examenfinalapp.model.Equipo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseEquiposRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : EquiposRepository {

    private val col = "equipos"

    override fun observarEquiposActivos(): Flow<List<Equipo>> = callbackFlow {
        val reg = db.collection(col)
            .whereEqualTo("activo", true)
            .orderBy("disponibles", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snap?.documents?.mapNotNull { d ->
                    val data = d.data ?: return@mapNotNull null
                    Equipo.fromMap(d.id, data)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    override suspend fun crearEquipo(equipo: Equipo): Result<Unit> = runCatching {
        // ID: usa el que venga en 'equipo.id' o deja que Firestore genere uno
        val ref = if (equipo.id.isBlank()) {
            db.collection(col).document()
        } else {
            db.collection(col).document(equipo.id)
        }
        ref.set(equipo.toMap()).await()
    }

    override suspend fun actualizarEquipo(equipo: Equipo): Result<Unit> = runCatching {
        require(equipo.id.isNotBlank()) { "El equipo necesita 'id' para actualizar" }
        db.collection(col).document(equipo.id).update(equipo.toMap()).await()
    }
}
