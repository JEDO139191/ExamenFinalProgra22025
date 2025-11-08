package com.example.examenfinalapp.data.firebase

import com.example.examenfinalapp.data.PrestamosRepository
import com.example.examenfinalapp.model.EstadoPrestamo
import com.example.examenfinalapp.model.Prestamo
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date

class FirebasePrestamosRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : PrestamosRepository {

    private val colPrestamos = "prestamos"
    private val colEquipos = "equipos"

    // ========================= OBSERVAR =========================

    override fun observarPrestamosUsuario(uid: String): Flow<List<Prestamo>> = callbackFlow {
        val reg = db.collection(colPrestamos)
            .whereEqualTo("uid", uid)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snap?.documents?.mapNotNull { d ->
                    val data = d.data ?: return@mapNotNull null
                    Prestamo.fromMap(d.id, data)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    override fun observarPendientes(): Flow<List<Prestamo>> = callbackFlow {
        val reg = db.collection(colPrestamos)
            .whereEqualTo("estado", EstadoPrestamo.Pendiente.name)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snap?.documents?.mapNotNull { d ->
                    val data = d.data ?: return@mapNotNull null
                    Prestamo.fromMap(d.id, data)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    // ========================= CREAR SOLICITUD =========================

    override suspend fun solicitarPrestamo(equipoId: String): Result<Unit> = runCatching {
        val uid = auth.currentUser?.uid ?: error("No hay usuario autenticado")
        val ahora = Timestamp.now()
        val cal = Calendar.getInstance().apply {
            time = Date(ahora.seconds * 1000L)
            add(Calendar.DATE, 3) // +3 días
        }
        val devolucion = Timestamp(cal.time)

        val data = hashMapOf<String, Any?>(
            "uid" to uid,
            "equipoId" to equipoId,
            "fechaPrestamo" to ahora,
            "fechaDevolucion" to devolucion,
            "estado" to EstadoPrestamo.Pendiente.name,
            "motivoRechazo" to null,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        db.collection(colPrestamos).add(data).await()
    }

    // ========================= ADMIN: APROBAR =========================

    override suspend fun aprobarPrestamo(prestamoId: String): Result<Unit> = runCatching {
        db.runTransaction { tx ->
            val pRef = db.collection(colPrestamos).document(prestamoId)
            val pSnap = tx.get(pRef)
            require(pSnap.exists()) { "Préstamo no encontrado" }

            val estadoActual = pSnap.getString("estado") ?: EstadoPrestamo.Pendiente.name
            require(estadoActual == EstadoPrestamo.Pendiente.name) { "Solo se aprueban préstamos Pendientes" }

            val equipoId = pSnap.getString("equipoId") ?: error("equipoId vacío")
            val eRef = db.collection(colEquipos).document(equipoId)
            val eSnap = tx.get(eRef)
            require(eSnap.exists()) { "Equipo no encontrado" }

            val disponibles = (eSnap.getLong("disponibles") ?: 0L).toInt()
            require(disponibles > 0) { "Sin equipos disponibles" }

            // Decrementa inventario y cambia estado
            tx.update(eRef, "disponibles", disponibles - 1)
            tx.update(pRef, mapOf(
                "estado" to EstadoPrestamo.Aprobado.name,
                "updatedAt" to FieldValue.serverTimestamp()
            ))
            null
        }.await()
    }

    // ========================= RECHAZAR =========================

    override suspend fun rechazarPrestamo(prestamoId: String, motivo: String?): Result<Unit> = runCatching {
        db.runTransaction { tx ->
            val pRef = db.collection(colPrestamos).document(prestamoId)
            val pSnap = tx.get(pRef)
            require(pSnap.exists()) { "Préstamo no encontrado" }

            val estadoActual = pSnap.getString("estado") ?: EstadoPrestamo.Pendiente.name
            // Se puede rechazar desde Pendiente (admin) o por auto-cancel del estudiante
            require(estadoActual == EstadoPrestamo.Pendiente.name) { "Solo se rechaza si está Pendiente" }

            tx.update(pRef, mapOf(
                "estado" to EstadoPrestamo.Rechazado.name,
                "motivoRechazo" to (motivo ?: ""),
                "updatedAt" to FieldValue.serverTimestamp()
            ))
            null
        }.await()
    }

    // ========================= MARCAR DEVUELTO =========================

    override suspend fun marcarDevuelto(prestamoId: String): Result<Unit> = runCatching {
        db.runTransaction { tx ->
            val pRef = db.collection(colPrestamos).document(prestamoId)
            val pSnap = tx.get(pRef)
            require(pSnap.exists()) { "Préstamo no encontrado" }

            val estadoActual = pSnap.getString("estado") ?: EstadoPrestamo.Pendiente.name
            require(estadoActual == EstadoPrestamo.Aprobado.name) { "Solo se devuelve si está Aprobado" }

            val equipoId = pSnap.getString("equipoId") ?: error("equipoId vacío")
            val eRef = db.collection(colEquipos).document(equipoId)
            val eSnap = tx.get(eRef)
            require(eSnap.exists()) { "Equipo no encontrado" }

            val disponibles = (eSnap.getLong("disponibles") ?: 0L).toInt()

            // Incrementa inventario y cierra préstamo
            tx.update(eRef, "disponibles", disponibles + 1)
            tx.update(pRef, mapOf(
                "estado" to EstadoPrestamo.Devuelto.name,
                "updatedAt" to FieldValue.serverTimestamp()
            ))
            null
        }.await()
    }
}
