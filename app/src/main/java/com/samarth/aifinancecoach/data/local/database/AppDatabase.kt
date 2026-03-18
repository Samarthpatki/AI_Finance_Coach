package com.samarth.aifinancecoach.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.samarth.aifinancecoach.data.local.dao.AiMessageDao
import com.samarth.aifinancecoach.data.local.dao.BudgetDao
import com.samarth.aifinancecoach.data.local.dao.TransactionDao
import com.samarth.aifinancecoach.data.local.entity.AiMessageEntity
import com.samarth.aifinancecoach.data.local.entity.BudgetEntity
import com.samarth.aifinancecoach.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class, BudgetEntity::class, AiMessageEntity::class],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun aiMessageDao(): AiMessageDao

    companion object {
        const val DATABASE_NAME = "ai_finance_coach_db"
    }
}
