package com.samarth.aifinancecoach.domain.repository

import com.samarth.aifinancecoach.domain.model.Budget
import com.samarth.aifinancecoach.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getBudgetsForMonth(month: Int, year: Int): Flow<List<Budget>>
    suspend fun insertOrUpdateBudget(budget: Budget)
    suspend fun deleteBudget(id: Long)
    suspend fun getBudgetForCategory(category: Category, month: Int, year: Int): Budget?
    suspend fun getBudgetById(id: Long): Budget?
}
