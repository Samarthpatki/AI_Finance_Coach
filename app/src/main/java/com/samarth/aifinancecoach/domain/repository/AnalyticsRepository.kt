package com.samarth.aifinancecoach.domain.repository

import com.samarth.aifinancecoach.domain.model.MonthlyAnalytics

interface AnalyticsRepository {
    suspend fun getMonthlyAnalytics(month: Int, year: Int): MonthlyAnalytics
}
