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
        // Sync to local preferences too
        dataStore.setUserName(profile.name)
        dataStore.setCurrency(profile.currency)
        dataStore.setMonthlyIncome(profile.monthlyIncome.toString())
        dataStore.setProfileSetupComplete(profile.profileSetupComplete)
    }

    override fun getUserProfile(): Flow<UserProfile?> = flow {
        // This would normally get the current user ID and fetch from Firestore
        // For a flow, you might want to listen to Firestore snapshots
    }

    override suspend fun updateUserProfile(profile: UserProfile) {
        userFirestore.updateUser(profile)
        dataStore.setUserName(profile.name)
        dataStore.setCurrency(profile.currency)
        dataStore.setMonthlyIncome(profile.monthlyIncome.toString())
    }
}
