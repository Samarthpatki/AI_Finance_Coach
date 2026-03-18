package com.samarth.aifinancecoach.domain.repository

import com.samarth.aifinancecoach.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun saveUserProfile(profile: UserProfile)
    fun getUserProfile(): Flow<UserProfile?>
    suspend fun updateUserProfile(profile: UserProfile)
}
