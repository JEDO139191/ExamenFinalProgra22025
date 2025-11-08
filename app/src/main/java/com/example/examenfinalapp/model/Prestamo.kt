package com.example.examenfinalapp.model

import com.google.firebase.Timestamp
import java.util.Date

/**
 * Documento en /prestamos/{prestamoId}
 *
 * Las fechas se almacenan en la app como Long (epoch millis).
 * En Firestore se guardan como Timestamp mediante los helpers toMap()/fromMap().
 */
data class Prestamo(
    val id: String = "",
    val uid: String = "",
    val equipoId: String = "",
    val fechaPrestamo: Long = 0L,
    val fechaDevolucion: Long = 0L,
    val estado: EstadoPrestamo = EstadoPrestamo.Pendiente,
    val motivoRechazo: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
) {
    /** Convierte a un mapa listo para Firestore (usa Timestamp). */
    fun toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "equipoId" to equipoId,
        "fechaPrestamo" to fechaPrestamo.toTimestamp(),
        "fechaDevolucion" to fechaDevolucion.toTimestamp(),
        "estado" to estado.name,
        "motivoRechazo" to motivoRechazo,
        "createdAt" to createdAt.toTimestamp(),
        "updatedAt" to updatedAt.toTimestamp()
    )

    companion object {
        /** Crea desde Firestore. 'id' viene del documento. */
        fun fromMap(id: String, map: Map<String, Any?>): Prestamo = Prestamo(
            id = id,
            uid = map["uid"] as? String ?: "",
            equipoId = map["equipoId"] as? String ?: "",
            fechaPrestamo = (map["fechaPrestamo"] as? Timestamp)?.toDate()?.time ?: 0L,
            fechaDevolucion = (map["fechaDevolucion"] as? Timestamp)?.toDate()?.time ?: 0L,
            estado = (map["estado"] as? String)?.let { safeEstado(it) } ?: EstadoPrestamo.Pendiente,
            motivoRechazo = map["motivoRechazo"] as? String,
            createdAt = (map["createdAt"] as? Timestamp)?.toDate()?.time ?: 0L,
            updatedAt = (map["updatedAt"] as? Timestamp)?.toDate()?.time ?: 0L
        )

        private fun safeEstado(value: String): EstadoPrestamo =
            runCatching { EstadoPrestamo.valueOf(value) }.getOrElse { EstadoPrestamo.Pendiente }
    }
}

/** Helpers de conversión Long <-> Timestamp (evita nulls). */
private fun Long.toTimestamp(): Timestamp =
    if (this > 0L) Timestamp(Date(this)) else Timestamp(Date(0L))
