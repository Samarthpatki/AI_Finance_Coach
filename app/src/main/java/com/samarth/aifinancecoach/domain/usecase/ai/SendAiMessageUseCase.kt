package com.samarth.aifinancecoach.domain.usecase.ai

import com.samarth.aifinancecoach.domain.model.AiMessage
import com.samarth.aifinancecoach.domain.repository.AiRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendAiMessageUseCase @Inject constructor(
    private val aiRepository: AiRepository,
    private val buildFinancialContextUseCase: BuildFinancialContextUseCase
) {
    suspend operator fun invoke(
        userMessage: String,
        conversationHistory: List<AiMessage>,
        month: Int,
        year: Int
    ): Flow<String> {
        val financialContext = buildFinancialContextUseCase(month, year)
        return aiRepository.sendMessage(userMessage, conversationHistory, financialContext)
    }
}
