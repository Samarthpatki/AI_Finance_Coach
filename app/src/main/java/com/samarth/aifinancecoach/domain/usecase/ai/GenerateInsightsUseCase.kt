package com.samarth.aifinancecoach.domain.usecase.ai

import com.samarth.aifinancecoach.domain.model.AiInsight
import com.samarth.aifinancecoach.domain.repository.AiRepository
import javax.inject.Inject

class GenerateInsightsUseCase @Inject constructor(
    private val aiRepository: AiRepository,
    private val buildFinancialContextUseCase: BuildFinancialContextUseCase
) {
    suspend operator fun invoke(month: Int, year: Int): List<AiInsight> {
        val context = buildFinancialContextUseCase(month, year)
        return aiRepository.generateInsights(context)
    }
}
