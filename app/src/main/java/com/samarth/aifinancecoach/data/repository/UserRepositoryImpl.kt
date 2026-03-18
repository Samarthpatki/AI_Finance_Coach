package com.samarth.aifinancecoach.data.repository

import com.samarth.aifinancecoach.data.datastore.UserPreferencesDataStore
import com.samarth.aifinancecoach.data.remote.firebase.firestore.UserFirestore
import com.samarth.aifinancecoach.domain.model.UserProfile
import com.samarth.aifinancecoach.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userFirestore: UserFirestore,
    private val dataStore: UserPreferencesDataStore
) : UserRepository {

    override suspend fun saveUserProfile(profile: UserProfile) {
        userFirestore.saveUser(profile)
    }

    override fun getUserProfile(): Flow<UserProfile?> = flow {
        // Implementation for getting user profile, e.g., from Firestore based on current user ID
        // For simplicity, we could also use dataStore to get some info or just use firestore
    }

    override suspend fun updateUserProfile(profile: UserProfile) {
        userFirestore.updateUser(profile)
    }
}
