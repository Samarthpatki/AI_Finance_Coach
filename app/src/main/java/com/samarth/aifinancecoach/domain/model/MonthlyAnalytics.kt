package com.samarth.aifinancecoach.domain.model

data class MonthlyAnalytics(
    val month: Int,
    val year: Int,
    val totalIncome: Double,
    val totalExpense: Double,
    val categoryBreakdown: Map<Category, Double>,
    val dailyExpenses: Map<Int, Double>
) {
    val netSavings: Double get() = totalIncome - totalExpense
    val savingsRate: Double get() = 
        if (totalIncome > 0) (netSavings / totalIncome) * 100 else 0.0
}
