package com.samarth.aifinancecoach.data.repository

import com.samarth.aifinancecoach.data.local.dao.BudgetDao
import com.samarth.aifinancecoach.data.mapper.BudgetMapper.toDomain
import com.samarth.aifinancecoach.data.mapper.BudgetMapper.toEntity
import com.samarth.aifinancecoach.domain.model.Budget
import com.samarth.aifinancecoach.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao
) : BudgetRepository {

    override fun getBudgetsForMonth(month: Int, year: Int): Flow<List<Budget>> {
        return budgetDao.getBudgetsForMonth(month, year).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertOrUpdateBudget(budget: Budget) {
        budgetDao.insertOrUpdate(budget.toEntity())
    }

    override suspend fun deleteBudget(id: Long) {
        budgetDao.delete(id)
    }
}
