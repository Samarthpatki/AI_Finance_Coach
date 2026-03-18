package com.samarth.aifinancecoach.domain.usecase.budget

import com.samarth.aifinancecoach.domain.model.Budget
import com.samarth.aifinancecoach.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBudgetsForMonthUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    operator fun invoke(month: Int, year: Int): Flow<List<Budget>> =
        repository.getBudgetsForMonth(month, year)
}
