package com.samarth.aifinancecoach.domain.repository

import com.samarth.aifinancecoach.domain.model.MonthlyAnalytics
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {
    fun getMonthlyAnalytics(month: Int, year: Int): Flow<MonthlyAnalytics>
}
