package com.example.examenfinalapp.model

/**
 * Documento en /usuarios/{uid}
 *
 * Notas:
 * - 'rol' debe ser exactamente "estudiante" o "admin".
 * - 'fotoUrl' se llenará cuando el usuario suba su foto (Storage).
 */
data class Usuario(
    val uid: String = "",
    val rol: String = "estudiante",        // "estudiante" | "admin"
    val nombreCompleto: String = "",
    val carnet: String = "",
    val carrera: String = "",
    val fotoUrl: String = ""
) {
    val esAdmin: Boolean get() = rol.equals("admin", ignoreCase = true)

    /** Mapa limpio para guardar en Firestore (opcional pero útil). */
    fun toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "rol" to rol,
        "nombreCompleto" to nombreCompleto,
        "carnet" to carnet,
        "carrera" to carrera,
        "fotoUrl" to fotoUrl
    )

    companion object {
        /** Construye desde un mapa Firestore sin crashear si faltan campos. */
        fun fromMap(map: Map<String, Any?>): Usuario = Usuario(
            uid = map["uid"] as? String ?: "",
            rol = map["rol"] as? String ?: "estudiante",
            nombreCompleto = map["nombreCompleto"] as? String ?: "",
            carnet = map["carnet"] as? String ?: "",
            carrera = map["carrera"] as? String ?: "",
            fotoUrl = map["fotoUrl"] as? String ?: ""
        )
    }
}
