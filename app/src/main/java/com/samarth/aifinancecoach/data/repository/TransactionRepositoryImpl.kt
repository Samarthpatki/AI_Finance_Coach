package com.samarth.aifinancecoach.data.repository

import com.samarth.aifinancecoach.data.local.dao.TransactionDao
import com.samarth.aifinancecoach.data.mapper.TransactionMapper.toDomain
import com.samarth.aifinancecoach.data.mapper.TransactionMapper.toEntity
import com.samarth.aifinancecoach.domain.model.Transaction
import com.samarth.aifinancecoach.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTransactionsByMonth(month: Int, year: Int): Flow<List<Transaction>> {
        return dao.getTransactionsByMonth(month, year).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecentTransactions(limit: Int): Flow<List<Transaction>> {
        return dao.getRecentTransactions(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertTransaction(transaction: Transaction): Long {
        return dao.insertTransaction(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        dao.updateTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(id: Long) {
        dao.deleteTransaction(id)
    }

    override suspend fun getTotalIncomeForMonth(month: Int, year: Int): Double {
        return dao.getTotalIncomeForMonth(month, year) ?: 0.0
    }

    override suspend fun getTotalExpenseForMonth(month: Int, year: Int): Double {
        return dao.getTotalExpenseForMonth(month, year) ?: 0.0
    }

    override suspend fun getTransactionById(id: Long): Transaction? {
        return dao.getTransactionById(id)?.toDomain()
    }

    override fun searchTransactions(query: String): Flow<List<Transaction>> {
        return dao.searchTransactions(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecurringTransactions(): Flow<List<Transaction>> {
        return dao.getRecurringTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}
