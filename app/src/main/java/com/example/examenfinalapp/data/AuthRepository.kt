package com.example.examenfinalapp.data

interface AuthRepository {
    val uidActual: String?
    suspend fun signUp(email: String, password: String): Result<String> // devuelve uid
    suspend fun signIn(email: String, password: String): Result<Unit>
    fun signOut()
}
