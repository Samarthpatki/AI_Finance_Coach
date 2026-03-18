package com.samarth.aifinancecoach.domain.usecase.budget

import com.samarth.aifinancecoach.domain.model.Budget
import com.samarth.aifinancecoach.domain.model.BudgetHealthLabel
import com.samarth.aifinancecoach.domain.model.BudgetHealthScore
import javax.inject.Inject

class GetBudgetHealthScoreUseCase @Inject constructor() {
    operator fun invoke(budgets: List<Budget>): BudgetHealthScore {
        if (budgets.isEmpty()) return BudgetHealthScore(100, BudgetHealthLabel.EXCELLENT)

        var score = 100
        budgets.forEach { budget ->
            val percentage = (budget.spentAmount / budget.limitAmount) * 100
            when {
                percentage >= 100 -> score -= 20
                percentage >= 80 -> score -= 8
            }
        }

        val clampedScore = score.coerceIn(0, 100)
        val label = when (clampedScore) {
            in 90..100 -> BudgetHealthLabel.EXCELLENT
            in 70..89 -> BudgetHealthLabel.GOOD
            in 50..69 -> BudgetHealthLabel.FAIR
            else -> BudgetHealthLabel.POOR
        }

        return BudgetHealthScore(clampedScore, label)
    }
}
