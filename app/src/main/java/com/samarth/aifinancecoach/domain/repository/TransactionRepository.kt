package com.samarth.aifinancecoach.domain.repository

import com.samarth.aifinancecoach.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsByMonth(month: Int, year: Int): Flow<List<Transaction>>
    fun getRecentTransactions(limit: Int): Flow<List<Transaction>>
    suspend fun insertTransaction(transaction: Transaction): Long
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(id: Long)
    suspend fun getTotalIncomeForMonth(month: Int, year: Int): Double
    suspend fun getTotalExpenseForMonth(month: Int, year: Int): Double
}
