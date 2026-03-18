package com.samarth.aifinancecoach.data.remote.firebase.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseStorageDataSource @Inject constructor(
    private val storage: FirebaseStorage
) {
    suspend fun uploadProfilePhoto(
        userId: String,
        imageUri: Uri
    ): String {
        val storageRef = storage.reference
            .child("profile_photos/${userId}.jpg")
        storageRef.putFile(imageUri).await()
        return storageRef.downloadUrl.await().toString()
    }
}
