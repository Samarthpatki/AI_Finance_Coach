package com.samarth.aifinancecoach.data.local.dao

import androidx.room.*
import com.samarth.aifinancecoach.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE month = :month AND year = :year ORDER BY dateMillis DESC")
    fun getTransactionsByMonth(month: Int, year: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)

    @Query("SELECT SUM(amount) FROM transactions WHERE month = :month AND year = :year AND type = 'INCOME'")
    suspend fun getTotalIncomeForMonth(month: Int, year: Int): Double?

    @Query("SELECT SUM(amount) FROM transactions WHERE month = :month AND year = :year AND type = 'EXPENSE'")
    suspend fun getTotalExpenseForMonth(month: Int, year: Int): Double?

    @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transactions WHERE note LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY dateMillis DESC")
    fun searchTransactions(query: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE isRecurring = 1 ORDER BY dateMillis DESC")
    fun getRecurringTransactions(): Flow<List<TransactionEntity>>
}
