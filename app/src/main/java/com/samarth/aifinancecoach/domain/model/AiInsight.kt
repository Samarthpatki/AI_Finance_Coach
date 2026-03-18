package com.samarth.aifinancecoach.domain.model

data class AiInsight(
    val id: Long = 0,
    val title: String,
    val description: String,
    val type: InsightType,
    val relatedCategory: Category? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

enum class InsightType {
    OVERSPENDING, SAVING_OPPORTUNITY, UNUSUAL_TRANSACTION,
    BUDGET_ALERT, MONTHLY_SUMMARY, POSITIVE_TREND
}
