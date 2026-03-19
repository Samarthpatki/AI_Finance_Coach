package com.samarth.aifinancecoach.data.repository

import com.samarth.aifinancecoach.data.datastore.UserPreferencesDataStore
import com.samarth.aifinancecoach.data.remote.firebase.auth.FirebaseAuthDataSource
import com.samarth.aifinancecoach.data.remote.firebase.firestore.UserFirestore
import com.samarth.aifinancecoach.domain.model.UserProfile
import com.samarth.aifinancecoach.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val userFirestore: UserFirestore,
    private val dataStore: UserPreferencesDataStore
) : AuthRepository {

    private suspend fun syncUserToLocal(userProfile: UserProfile) {
        dataStore.setUserName(userProfile.name)
        dataStore.setCurrency(userProfile.currency)
        dataStore.setMonthlyIncome(userProfile.monthlyIncome.toString())
        dataStore.setProfileSetupComplete(userProfile.profileSetupComplete)
    }

    override suspend fun signInWithGoogle(idToken: String): Result<UserProfile> {
        val result = authDataSource.signInWithGoogle(idToken)
        if (result.isSuccess) {
            val userProfile = result.getOrThrow()
            // Check if user already exists in Firestore to preserve their setup state
            val existingUser = userFirestore.getUser(userProfile.id)
            val finalProfile = existingUser ?: userProfile
            
            if (existingUser == null) {
                userFirestore.saveUser(userProfile)
            }
            
            // Sync to local cache so MainViewModel knows setup is complete
            syncUserToLocal(finalProfile)
            
            return Result.success(finalProfile)
        }
        return result
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<UserProfile> {
        val result = authDataSource.signInWithEmail(email, password)
        if (result.isSuccess) {
            val userProfile = result.getOrThrow()
            val existingUser = userFirestore.getUser(userProfile.id)
            val finalProfile = existingUser ?: userProfile
            
            // Sync to local cache
            syncUserToLocal(finalProfile)
            
            return Result.success(finalProfile)
        }
        return result
    }

    override suspend fun signUpWithEmail(name: String, email: String, password: String): Result<UserProfile> {
        val result = authDataSource.signUpWithEmail(name, email, password)
        if (result.isSuccess) {
            val userProfile = result.getOrThrow()
            userFirestore.saveUser(userProfile)
            // Sync to local cache (setup will be false initially)
            syncUserToLocal(userProfile)
        }
        return result
    }

    override suspend fun signOut() {
        authDataSource.signOut()
        // Clear local setup flag on sign out
        dataStore.setProfileSetupComplete(false)
        dataStore.setUserName("")
    }

    override suspend fun getCurrentUser(): UserProfile? {
        val authUser = authDataSource.getCurrentUser() ?: return null
        val firestoreUser = userFirestore.getUser(authUser.id)
        return firestoreUser ?: authUser
    }

    override fun isUserLoggedIn(): Boolean {
        return authDataSource.isUserLoggedIn()
    }
}
