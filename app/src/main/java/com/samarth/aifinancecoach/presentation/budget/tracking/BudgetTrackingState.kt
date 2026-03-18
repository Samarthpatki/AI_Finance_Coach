package com.samarth.aifinancecoach.presentation.budget.tracking

import com.samarth.aifinancecoach.domain.model.Budget
import com.samarth.aifinancecoach.domain.model.BudgetHealthScore
import com.samarth.aifinancecoach.utils.DateUtils

data class BudgetTrackingState(
    val budgets: List<Budget> = emptyList(),
    val selectedMonth: Int = DateUtils.getCurrentMonth(),
    val selectedYear: Int = DateUtils.getCurrentYear(),
    val totalBudgeted: Double = 0.0,
    val totalSpent: Double = 0.0,
    val healthScore: BudgetHealthScore? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val totalRemaining: Double get() = totalBudgeted - totalSpent
    val overallProgress: Float get() =
        if (totalBudgeted > 0)
            (totalSpent / totalBudgeted).toFloat().coerceIn(0f, 1.1f)
        else 0f
}
