package com.samarth.aifinancecoach.data.local.dao

import androidx.room.*
import com.samarth.aifinancecoach.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("""
        SELECT * FROM transactions 
        WHERE strftime('%m', dateMillis/1000, 'unixepoch') = printf('%02d', :month) 
        AND strftime('%Y', dateMillis/1000, 'unixepoch') = CAST(:year AS TEXT)
    """)
    fun getTransactionsByMonth(month: Int, year: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)

    @Query("""
        SELECT SUM(amount) FROM transactions 
        WHERE type = 'INCOME' 
        AND strftime('%m', dateMillis/1000, 'unixepoch') = printf('%02d', :month) 
        AND strftime('%Y', dateMillis/1000, 'unixepoch') = CAST(:year AS TEXT)
    """)
    suspend fun getTotalIncome(month: Int, year: Int): Double?

    @Query("""
        SELECT SUM(amount) FROM transactions 
        WHERE type = 'EXPENSE' 
        AND strftime('%m', dateMillis/1000, 'unixepoch') = printf('%02d', :month) 
        AND strftime('%Y', dateMillis/1000, 'unixepoch') = CAST(:year AS TEXT)
    """)
    suspend fun getTotalExpense(month: Int, year: Int): Double?
}
