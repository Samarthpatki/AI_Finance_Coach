package com.samarth.aifinancecoach.domain.model

data class BudgetHealthScore(
    val score: Int,
    val label: BudgetHealthLabel
)

enum class BudgetHealthLabel {
    EXCELLENT, GOOD, FAIR, POOR
}
