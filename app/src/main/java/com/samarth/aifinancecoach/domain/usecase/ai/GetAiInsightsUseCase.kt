package com.samarth.aifinancecoach.domain.usecase.ai

import com.samarth.aifinancecoach.domain.model.AiInsight
import com.samarth.aifinancecoach.domain.repository.AiRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAiInsightsUseCase @Inject constructor(
    private val repository: AiRepository
) {
    operator fun invoke(): Flow<List<AiInsight>> = repository.getAiInsights()
}
