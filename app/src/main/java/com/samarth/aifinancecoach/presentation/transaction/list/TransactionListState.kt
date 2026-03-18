package com.samarth.aifinancecoach.presentation.transaction.list

import com.samarth.aifinancecoach.domain.model.Transaction
import com.samarth.aifinancecoach.utils.DateUtils

data class TransactionListState(
    val allTransactions: List<Transaction> = emptyList(),
    val filteredTransactions: List<Transaction> = emptyList(),
    val groupedTransactions: Map<String, List<Transaction>> = emptyMap(),
    val searchQuery: String = "",
    val selectedFilter: TransactionFilter = TransactionFilter.ALL,
    val selectedMonth: Int = DateUtils.getCurrentMonth(),
    val selectedYear: Int = DateUtils.getCurrentYear(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class TransactionFilter {
    ALL, INCOME, EXPENSE
}
