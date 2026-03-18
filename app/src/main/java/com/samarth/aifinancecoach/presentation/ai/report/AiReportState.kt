package com.samarth.aifinancecoach.presentation.ai.report

import com.samarth.aifinancecoach.domain.model.MonthlyAnalytics
import java.util.Calendar

data class AiReportState(
    val reportContent: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    val currentYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val monthlyAnalytics: MonthlyAnalytics? = null
)
