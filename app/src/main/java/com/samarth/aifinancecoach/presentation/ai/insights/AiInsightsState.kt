package com.samarth.aifinancecoach.presentation.ai.insights

import com.samarth.aifinancecoach.domain.model.AiInsight
import java.util.Calendar

data class AiInsightsState(
    val insights: List<AiInsight> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    val currentYear: Int = Calendar.getInstance().get(Calendar.YEAR)
)
