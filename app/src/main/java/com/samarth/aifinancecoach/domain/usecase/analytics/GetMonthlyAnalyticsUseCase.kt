package com.samarth.aifinancecoach.domain.usecase.analytics

import com.samarth.aifinancecoach.domain.model.MonthlyAnalytics
import com.samarth.aifinancecoach.domain.repository.AnalyticsRepository
import javax.inject.Inject

class GetMonthlyAnalyticsUseCase @Inject constructor(
    private val repository: AnalyticsRepository
) {
    suspend operator fun invoke(month: Int, year: Int): MonthlyAnalytics =
        repository.getMonthlyAnalytics(month, year)
}
