package com.samarth.aifinancecoach.presentation.dashboard

import com.samarth.aifinancecoach.domain.model.AiInsight
import com.samarth.aifinancecoach.domain.model.Budget
import com.samarth.aifinancecoach.domain.model.Transaction
import com.samarth.aifinancecoach.utils.DateUtils

data class DashboardState(
    val userName: String = "",
    val currentMonth: Int = DateUtils.getCurrentMonth(),
    val currentYear: Int = DateUtils.getCurrentYear(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val budgets: List<Budget> = emptyList(),
    val aiInsights: List<AiInsight> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val netSavings: Double get() = totalIncome - totalExpense
    val savingsRate: Double get() =
        if (totalIncome > 0) (netSavings / totalIncome) * 100 else 0.0
}
