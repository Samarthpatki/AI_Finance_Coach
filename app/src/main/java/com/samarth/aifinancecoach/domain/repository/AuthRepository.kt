package com.samarth.aifinancecoach.domain.repository

import com.samarth.aifinancecoach.domain.model.UserProfile

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): Result<UserProfile>
    suspend fun signInWithEmail(email: String, password: String): Result<UserProfile>
    suspend fun signUpWithEmail(name: String, email: String, password: String): Result<UserProfile>
    suspend fun signOut()
    suspend fun getCurrentUser(): UserProfile?
    fun isUserLoggedIn(): Boolean
}
