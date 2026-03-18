package com.samarth.aifinancecoach.domain.usecase.auth

import com.samarth.aifinancecoach.domain.repository.AuthRepository
import javax.inject.Inject

class IsUserLoggedInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Boolean = repository.isUserLoggedIn()
}
