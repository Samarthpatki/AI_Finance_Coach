package com.samarth.aifinancecoach.domain.usecase.analytics

import com.samarth.aifinancecoach.domain.model.MonthlyAnalytics
import com.samarth.aifinancecoach.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMonthlyAnalyticsUseCase @Inject constructor(
    private val repository: AnalyticsRepository
) {
    operator fun invoke(month: Int, year: Int): Flow<MonthlyAnalytics> =
        repository.getMonthlyAnalytics(month, year)
}
