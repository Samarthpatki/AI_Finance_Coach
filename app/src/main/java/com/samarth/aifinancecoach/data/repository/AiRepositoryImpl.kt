package com.samarth.aifinancecoach.data.repository

import com.samarth.aifinancecoach.data.local.dao.AiMessageDao
import com.samarth.aifinancecoach.data.mapper.AiMessageMapper.toDomain
import com.samarth.aifinancecoach.data.mapper.AiMessageMapper.toEntity
import com.samarth.aifinancecoach.data.remote.api.GeminiDataSource
import com.samarth.aifinancecoach.domain.model.AiInsight
import com.samarth.aifinancecoach.domain.model.AiMessage
import com.samarth.aifinancecoach.domain.model.FinancialContext
import com.samarth.aifinancecoach.domain.model.MessageRole
import com.samarth.aifinancecoach.domain.repository.AiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepositoryImpl @Inject constructor(
    private val geminiDataSource: GeminiDataSource,
    private val aiMessageDao: AiMessageDao
) : AiRepository {

    override suspend fun sendMessage(
        userMessage: String,
        conversationHistory: List<AiMessage>,
        financialContext: FinancialContext
    ): Flow<String> {
        // Save user message to DB
        val userMessageEntity = AiMessage(
            role = MessageRole.USER,
            content = userMessage
        ).toEntity()
        aiMessageDao.insertMessage(userMessageEntity)

        val fullResponse = StringBuilder()
        
        return geminiDataSource.sendMessage(userMessage, conversationHistory, financialContext)
            .map { token ->
                fullResponse.append(token)
                token
            }
            .onCompletion { cause ->
                if (cause == null && fullResponse.isNotEmpty()) {
                    // Save assistant message to DB when streaming is complete
                    val assistantMessageEntity = AiMessage(
                        role = MessageRole.ASSISTANT,
                        content = fullResponse.toString()
                    ).toEntity()
                    aiMessageDao.insertMessage(assistantMessageEntity)
                }
            }
    }

    override suspend fun generateInsights(context: FinancialContext): List<AiInsight> {
        return geminiDataSource.generateInsights(context)
    }

    override suspend fun generateMonthlyReport(context: FinancialContext): String {
        return geminiDataSource.generateMonthlyReport(context)
    }

    override fun getChatHistory(): Flow<List<AiMessage>> {
        return aiMessageDao.getAllMessages().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveChatMessage(message: AiMessage) {
        aiMessageDao.insertMessage(message.toEntity())
    }

    override suspend fun clearChatHistory() {
        aiMessageDao.clearAll()
    }

    override fun getAiInsights(): Flow<List<AiInsight>> {
        // Temporary placeholder to prevent crash while local storage for insights is not yet implemented
        return flowOf(emptyList())
    }
}
