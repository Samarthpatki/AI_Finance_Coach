package com.samarth.aifinancecoach.data.repository

import com.samarth.aifinancecoach.domain.model.AiInsight
import com.samarth.aifinancecoach.domain.repository.AiRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor() : AiRepository {
    override fun getAiInsights(): Flow<List<AiInsight>> {
        TODO("Not yet implemented")
    }
}
