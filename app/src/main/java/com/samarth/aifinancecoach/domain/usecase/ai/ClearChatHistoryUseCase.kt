package com.samarth.aifinancecoach.domain.usecase.ai

import com.samarth.aifinancecoach.domain.repository.AiRepository
import javax.inject.Inject

class ClearChatHistoryUseCase @Inject constructor(
    private val aiRepository: AiRepository
) {
    suspend operator fun invoke() {
        aiRepository.clearChatHistory()
    }
}
