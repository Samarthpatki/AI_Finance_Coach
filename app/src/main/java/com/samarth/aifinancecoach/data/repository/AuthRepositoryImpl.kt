package com.samarth.aifinancecoach.data.repository

import com.samarth.aifinancecoach.data.remote.firebase.auth.FirebaseAuthDataSource
import com.samarth.aifinancecoach.data.remote.firebase.firestore.UserFirestore
import com.samarth.aifinancecoach.domain.model.UserProfile
import com.samarth.aifinancecoach.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val userFirestore: UserFirestore
) : AuthRepository {

    override suspend fun signInWithGoogle(idToken: String): Result<UserProfile> {
        val result = authDataSource.signInWithGoogle(idToken)
        if (result.isSuccess) {
            val userProfile = result.getOrThrow()
            userFirestore.saveUser(userProfile)
        }
        return result
    }

    override suspend fun signOut() {
        authDataSource.signOut()
    }

    override suspend fun getCurrentUser(): UserProfile? {
        return authDataSource.getCurrentUser()
    }

    override fun isUserLoggedIn(): Boolean {
        return authDataSource.isUserLoggedIn()
    }
}
