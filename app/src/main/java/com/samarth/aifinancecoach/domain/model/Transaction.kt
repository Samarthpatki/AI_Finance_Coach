package com.samarth.aifinancecoach.domain.model

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val account: AccountType,
    val note: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val month: Int,
    val year: Int,
    val isRecurring: Boolean = false,
    val recurringIntervalDays: Int = 30,
    val tags: List<String> = emptyList()
)

enum class TransactionType {
    INCOME, EXPENSE
}

enum class AccountType {
    CASH, UPI, BANK
}
