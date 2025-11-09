package com.example.examenfinalapp.model

enum class RolUsuario { Estudiante, Administrador }

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val carnet: String = "",
    val carrera: String = "",
    val email: String = "",
    val fotoUrl: String? = null,
    val rol: RolUsuario = RolUsuario.Estudiante
) {
    val esAdmin: Boolean get() = rol == RolUsuario.Administrador

    fun toMap(): Map<String, Any?> = mapOf(
        "nombre" to nombre,
        "carnet" to carnet,
        "carrera" to carrera,
        "email" to email,
        "fotoUrl" to fotoUrl,
        "rol" to rol.name
    )

    companion object {
        fun fromMap(map: Map<String, Any?>): Usuario = Usuario(
            uid = "",
            nombre = map["nombre"] as? String ?: "",
            carnet = map["carnet"] as? String ?: "",
            carrera = map["carrera"] as? String ?: "",
            email = map["email"] as? String ?: "",
            fotoUrl = map["fotoUrl"] as? String,
            rol = (map["rol"] as? String)?.let {
                runCatching { RolUsuario.valueOf(it) }.getOrNull()
            } ?: RolUsuario.Estudiante
        )
    }
}
