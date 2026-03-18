package com.samarth.aifinancecoach.domain.usecase.user

import com.samarth.aifinancecoach.data.datastore.UserPreferencesDataStore
import com.samarth.aifinancecoach.domain.model.UserProfile
import com.samarth.aifinancecoach.domain.repository.UserRepository
import javax.inject.Inject

class SaveUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val dataStore: UserPreferencesDataStore
) {
    suspend operator fun invoke(profile: UserProfile) {
        userRepository.saveUserProfile(profile)
        dataStore.setProfileSetupComplete(true)
    }
}
