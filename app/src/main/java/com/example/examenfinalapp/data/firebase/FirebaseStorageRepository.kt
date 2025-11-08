package com.example.examenfinalapp.data.firebase

import android.net.Uri
import com.example.examenfinalapp.data.StorageRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseStorageRepository(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : StorageRepository {

    override suspend fun uploadUserPhoto(uid: String, fileUri: Uri): Result<String> = runCatching {
        val ref = storage.getReference("users/$uid/profile.jpg")
        ref.putFile(fileUri).await()
        ref.downloadUrl.await().toString()
    }

    override suspend fun uploadEquipoImage(equipoId: String, fileUri: Uri): Result<String> = runCatching {
        val ref = storage.getReference("equipos/$equipoId/imagen.jpg")
        ref.putFile(fileUri).await()
        ref.downloadUrl.await().toString()
    }
}
