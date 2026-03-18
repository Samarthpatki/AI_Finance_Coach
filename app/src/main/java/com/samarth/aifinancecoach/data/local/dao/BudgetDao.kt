package com.samarth.aifinancecoach.data.local.dao

import androidx.room.*
import com.samarth.aifinancecoach.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year")
    fun getBudgetsForMonth(month: Int, year: Int): Flow<List<BudgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(budget: BudgetEntity)

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM budgets WHERE category = :category AND month = :month AND year = :year LIMIT 1")
    suspend fun getBudgetForCategory(category: String, month: Int, year: Int): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE id = :id LIMIT 1")
    suspend fun getBudgetById(id: Long): BudgetEntity?
}
