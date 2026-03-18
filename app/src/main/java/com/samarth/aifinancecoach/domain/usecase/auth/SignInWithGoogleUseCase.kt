package com.samarth.aifinancecoach.domain.usecase.auth

import com.samarth.aifinancecoach.domain.model.UserProfile
import com.samarth.aifinancecoach.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<UserProfile> =
        repository.signInWithGoogle(idToken)
}
