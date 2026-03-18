package com.samarth.aifinancecoach.domain.usecase.ai

import com.samarth.aifinancecoach.domain.model.AiMessage
import com.samarth.aifinancecoach.domain.repository.AiRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatHistoryUseCase @Inject constructor(
    private val aiRepository: AiRepository
) {
    operator fun invoke(): Flow<List<AiMessage>> {
        return aiRepository.getChatHistory()
    }
}
