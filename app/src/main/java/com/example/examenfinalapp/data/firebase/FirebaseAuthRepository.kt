package com.example.examenfinalapp.data.firebase

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository {

    private val auth = FirebaseAuth.getInstance()

    val uidActual: String?
        get() = auth.currentUser?.uid

    suspend fun signUp(email: String, password: String): Result<String> = runCatching {
        val res = auth.createUserWithEmailAndPassword(email, password).await()
        res.user?.uid ?: throw Exception("UID nulo")
    }

    suspend fun signIn(email: String, password: String): Result<String> = runCatching {
        val res = auth.signInWithEmailAndPassword(email, password).await()
        res.user?.uid ?: throw Exception("UID nulo")
    }

    fun signOut() {
        auth.signOut()
    }
}
