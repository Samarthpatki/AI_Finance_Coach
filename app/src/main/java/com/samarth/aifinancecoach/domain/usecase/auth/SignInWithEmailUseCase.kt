package com.samarth.aifinancecoach.domain.usecase.auth

import com.samarth.aifinancecoach.domain.model.UserProfile
import com.samarth.aifinancecoach.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithEmailUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<UserProfile> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Email and password cannot be empty"))
        }
        return repository.signInWithEmail(email, password)
    }
}
