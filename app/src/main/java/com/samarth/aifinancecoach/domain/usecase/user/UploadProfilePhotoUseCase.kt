package com.samarth.aifinancecoach.domain.usecase.user

import android.net.Uri
import com.samarth.aifinancecoach.data.remote.firebase.storage.FirebaseStorageDataSource
import javax.inject.Inject

class UploadProfilePhotoUseCase @Inject constructor(
    private val storageDataSource: FirebaseStorageDataSource
) {
    suspend operator fun invoke(userId: String, imageUri: Uri): String {
        return storageDataSource.uploadProfilePhoto(userId, imageUri)
    }
}
