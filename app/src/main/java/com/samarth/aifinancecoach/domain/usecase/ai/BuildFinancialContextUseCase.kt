package com.samarth.aifinancecoach.domain.usecase.ai

import com.samarth.aifinancecoach.data.datastore.UserPreferencesDataStore
import com.samarth.aifinancecoach.domain.model.FinancialContext
import com.samarth.aifinancecoach.domain.repository.BudgetRepository
import com.samarth.aifinancecoach.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BuildFinancialContextUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository,
    private val userPreferencesDataStore: UserPreferencesDataStore
) {
    suspend operator fun invoke(month: Int, year: Int): FinancialContext {
        val userName = userPreferencesDataStore.getUserName().first()
        val currency = userPreferencesDataStore.getCurrency().first()
        
        val transactionsForMonth = transactionRepository.getTransactionsByMonth(month, year).first()
        val last30Transactions = transactionRepository.getRecentTransactions(30).first()
        
        val totalIncome = transactionRepository.getTotalIncomeForMonth(month, year)
        val totalExpense = transactionRepository.getTotalExpenseForMonth(month, year)
        
        val savingsRate = if (totalIncome > 0) {
            ((totalIncome - totalExpense) / totalIncome) * 100
        } else {
            0.0
        }
        
        val budgets = budgetRepository.getBudgetsForMonth(month, year).first()
        
        val topSpendingCategories = transactionsForMonth
            .filter { it.type == com.samarth.aifinancecoach.domain.model.TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
            .take(5)
            
        return FinancialContext(
            userName = userName,
            currentMonth = month,
            currentYear = year,
            monthlyIncome = totalIncome,
            monthlyExpense = totalExpense,
            savingsRate = savingsRate,
            recentTransactions = last30Transactions,
            budgets = budgets,
            topSpendingCategories = topSpendingCategories,
            currency = currency,
            currencySymbol = if (currency == "INR") "₹" else "$"
        )
    }
}
