package com.samarth.aifinancecoach.domain.usecase.user

import com.samarth.aifinancecoach.data.datastore.UserPreferencesDataStore
import com.samarth.aifinancecoach.domain.model.UserProfile
import com.samarth.aifinancecoach.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore
) {
    suspend operator fun invoke(profile: UserProfile) {
        userRepository.updateUserProfile(profile)
        userPreferencesDataStore.setUserName(profile.name)
        userPreferencesDataStore.setMonthlyIncome(profile.monthlyIncome.toString())
    }
}
