package com.samarth.aifinancecoach.domain.usecase.budget

import com.samarth.aifinancecoach.domain.model.Budget
import com.samarth.aifinancecoach.domain.repository.BudgetRepository
import javax.inject.Inject

class SetBudgetUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(budget: Budget) {
        repository.insertOrUpdateBudget(budget)
    }
}
