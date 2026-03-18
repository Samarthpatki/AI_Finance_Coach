package com.samarth.aifinancecoach.domain.repository

import com.samarth.aifinancecoach.domain.model.AiInsight
import kotlinx.coroutines.flow.Flow


interface AiRepository {
    fun getAiInsights(): Flow<List<AiInsight>>
}
