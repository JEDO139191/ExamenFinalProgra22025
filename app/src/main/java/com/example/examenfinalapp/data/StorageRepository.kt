package com.example.examenfinalapp.data

import android.net.Uri

interface StorageRepository {
    suspend fun uploadUserPhoto(uid: String, fileUri: Uri): Result<String>   // devuelve downloadUrl
    suspend fun uploadEquipoImage(equipoId: String, fileUri: Uri): Result<String>
}
