package com.example.examenfinalapp.model

/**
 * Documento en /equipos/{equipoId}
 *
 * 'id' no se guarda en el documento, lo vamos a asignar desde el id del doc.
 * 'disponibles' debe estar entre 0 y 'total'.
 */
data class Equipo(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val imagenUrl: String = "",
    val total: Int = 0,
    val disponibles: Int = 0,
    val activo: Boolean = true,
    val tags: List<String> = emptyList()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "nombre" to nombre,
        "descripcion" to descripcion,
        "imagenUrl" to imagenUrl,
        "total" to total,
        "disponibles" to disponibles,
        "activo" to activo,
        "tags" to tags
    )

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): Equipo = Equipo(
            id = id,
            nombre = map["nombre"] as? String ?: "",
            descripcion = map["descripcion"] as? String ?: "",
            imagenUrl = map["imagenUrl"] as? String ?: "",
            total = (map["total"] as? Number)?.toInt() ?: 0,
            disponibles = (map["disponibles"] as? Number)?.toInt() ?: 0,
            activo = map["activo"] as? Boolean ?: true,
            tags = (map["tags"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
        )
    }
}
