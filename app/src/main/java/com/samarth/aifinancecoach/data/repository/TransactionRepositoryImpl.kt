package com.samarth.aifinancecoach.data.repository

import com.samarth.aifinancecoach.data.local.dao.TransactionDao
import com.samarth.aifinancecoach.data.mapper.TransactionMapper.toDomain
import com.samarth.aifinancecoach.data.mapper.TransactionMapper.toEntity
import com.samarth.aifinancecoach.domain.model.Transaction
import com.samarth.aifinancecoach.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTransactionsByMonth(month: Int, year: Int): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByMonth(month, year).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecentTransactions(limit: Int): Flow<List<Transaction>> {
        return transactionDao.getRecentTransactions(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertTransaction(transaction: Transaction): Long {
        return transactionDao.insertTransaction(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(id: Long) {
        transactionDao.deleteTransaction(id)
    }

    override suspend fun getTotalIncomeForMonth(month: Int, year: Int): Double {
        return transactionDao.getTotalIncome(month, year) ?: 0.0
    }

    override suspend fun getTotalExpenseForMonth(month: Int, year: Int): Double {
        return transactionDao.getTotalExpense(month, year) ?: 0.0
    }
}
