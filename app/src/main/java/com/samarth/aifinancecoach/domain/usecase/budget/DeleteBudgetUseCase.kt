package com.samarth.aifinancecoach.domain.usecase.budget

import com.samarth.aifinancecoach.domain.repository.BudgetRepository
import javax.inject.Inject

class DeleteBudgetUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteBudget(id)
    }
}
