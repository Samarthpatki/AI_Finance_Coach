package com.samarth.aifinancecoach.data.repository

import com.samarth.aifinancecoach.data.local.dao.TransactionDao
import com.samarth.aifinancecoach.data.mapper.TransactionMapper.toDomain
import com.samarth.aifinancecoach.domain.model.Category
import com.samarth.aifinancecoach.domain.model.MonthlyAnalytics
import com.samarth.aifinancecoach.domain.model.TransactionType
import com.samarth.aifinancecoach.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.first
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : AnalyticsRepository {

    override suspend fun getMonthlyAnalytics(month: Int, year: Int): MonthlyAnalytics {
        val transactions = transactionDao.getTransactionsByMonth(month, year).first().map { it.toDomain() }
        
        var totalIncome = 0.0
        var totalExpense = 0.0
        val categoryBreakdown = mutableMapOf<Category, Double>()
        val dailyExpenses = mutableMapOf<Int, Double>()

        transactions.forEach { transaction ->
            if (transaction.type == TransactionType.INCOME) {
                totalIncome += transaction.amount
            } else {
                totalExpense += transaction.amount
                
                // Category breakdown
                val currentCategoryTotal = categoryBreakdown[transaction.category] ?: 0.0
                categoryBreakdown[transaction.category] = currentCategoryTotal + transaction.amount
                
                // Daily expenses
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = transaction.dateMillis
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val currentDayTotal = dailyExpenses[day] ?: 0.0
                dailyExpenses[day] = currentDayTotal + transaction.amount
            }
        }

        return MonthlyAnalytics(
            month = month,
            year = year,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            categoryBreakdown = categoryBreakdown,
            dailyExpenses = dailyExpenses
        )
    }
}
