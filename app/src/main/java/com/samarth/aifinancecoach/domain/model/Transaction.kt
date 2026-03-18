package com.samarth.aifinancecoach.domain.model

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val account: AccountType,
    val note: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val isRecurring: Boolean = false,
    val recurringIntervalDays: Int? = null,
    val tags: List<String> = emptyList()
)

enum class TransactionType {
    INCOME, EXPENSE
}

enum class AccountType {
    CASH, UPI, BANK, CREDIT_CARD
}

enum class Category(val label: String, val emoji: String) {
    FOOD("Food & Dining", "🍔"),
    TRANSPORT("Transport", "🚗"),
    SHOPPING("Shopping", "🛍️"),
    BILLS("Bills & Utilities", "💡"),
    EMI("EMI & Loans", "🏦"),
    HEALTH("Health & Medical", "💊"),
    ENTERTAINMENT("Entertainment", "🎬"),
    INVESTMENT("Investment", "📈"),
    SALARY("Salary", "💰"),
    FREELANCE("Freelance", "💻"),
    GROCERIES("Groceries", "🛒"),
    EDUCATION("Education", "📚"),
    TRAVEL("Travel", "✈️"),
    OTHER("Other", "📦")
}
