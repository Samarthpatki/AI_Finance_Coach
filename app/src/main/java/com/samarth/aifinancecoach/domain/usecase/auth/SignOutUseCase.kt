package com.samarth.aifinancecoach.domain.usecase.auth

import com.samarth.aifinancecoach.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.signOut()
}
