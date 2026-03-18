package com.samarth.aifinancecoach.data.remote.firebase.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.samarth.aifinancecoach.data.mapper.UserProfileMapper.toDomain
import com.samarth.aifinancecoach.data.mapper.UserProfileMapper.toDto
import com.samarth.aifinancecoach.data.remote.dto.UserProfileDto
import com.samarth.aifinancecoach.domain.model.UserProfile
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserFirestore @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val userCollection = firestore.collection("users")

    suspend fun saveUser(profile: UserProfile) {
        userCollection.document(profile.id).set(profile.toDto()).await()
    }

    suspend fun getUser(userId: String): UserProfile? {
        return try {
            userCollection.document(userId).get().await()
                .toObject(UserProfileDto::class.java)?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUser(profile: UserProfile) {
        userCollection.document(profile.id).set(profile.toDto()).await()
    }
}
