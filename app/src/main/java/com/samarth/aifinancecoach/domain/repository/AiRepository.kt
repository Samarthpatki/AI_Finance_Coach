package com.samarth.aifinancecoach.domain.repository

import com.samarth.aifinancecoach.domain.model.AiInsight
import com.samarth.aifinancecoach.domain.model.AiMessage
import com.samarth.aifinancecoach.domain.model.FinancialContext
import kotlinx.coroutines.flow.Flow

interface AiRepository {
    suspend fun sendMessage(
        userMessage: String,
        conversationHistory: List<AiMessage>,
        financialContext: FinancialContext
    ): Flow<String>

    suspend fun generateInsights(
        context: FinancialContext
    ): List<AiInsight>

    suspend fun generateMonthlyReport(
        context: FinancialContext
    ): String

    fun getChatHistory(): Flow<List<AiMessage>>
    suspend fun saveChatMessage(message: AiMessage)
    suspend fun clearChatHistory()
    fun getAiInsights(): Flow<List<AiInsight>>
}
