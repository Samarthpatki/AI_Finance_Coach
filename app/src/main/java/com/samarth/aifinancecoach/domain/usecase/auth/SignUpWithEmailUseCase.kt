package com.samarth.aifinancecoach.domain.usecase.auth

import com.samarth.aifinancecoach.domain.model.UserProfile
import com.samarth.aifinancecoach.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpWithEmailUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String): Result<UserProfile> {
        if (name.isBlank()) {
            return Result.failure(Exception("Name cannot be empty"))
        }
        if (email.isBlank()) {
            return Result.failure(Exception("Email cannot be empty"))
        }
        if (password.length < 6) {
            return Result.failure(Exception("Password must be at least 6 characters"))
        }
        return repository.signUpWithEmail(name, email, password)
    }
}
