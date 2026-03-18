package com.samarth.aifinancecoach.domain.repository

import com.samarth.aifinancecoach.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getBudgetsForMonth(month: Int, year: Int): Flow<List<Budget>>
    suspend fun insertOrUpdateBudget(budget: Budget)
    suspend fun deleteBudget(id: Long)
}
