package com.samarth.aifinancecoach.domain.model

data class FinancialContext(
    val userName: String,
    val currentMonth: Int,
    val currentYear: Int,
    val monthlyIncome: Double,
    val monthlyExpense: Double,
    val savingsRate: Double,
    val recentTransactions: List<Transaction>,
    val budgets: List<Budget>,
    val topSpendingCategories: List<Pair<Category, Double>>,
    val currency: String = "INR",
    val currencySymbol: String = "₹"
)
