package com.samarth.aifinancecoach.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: String,
    val category: String,
    val account: String,
    val note: String = "",
    val dateMillis: Long,
    val month: Int,
    val year: Int,
    val isRecurring: Boolean = false,
    val recurringIntervalDays: Int? = null,
    val tags: String = ""
)
