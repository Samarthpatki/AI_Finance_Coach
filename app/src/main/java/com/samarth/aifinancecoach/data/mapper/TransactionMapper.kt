package com.samarth.aifinancecoach.data.mapper

import com.samarth.aifinancecoach.data.local.entity.TransactionEntity
import com.samarth.aifinancecoach.domain.model.AccountType
import com.samarth.aifinancecoach.domain.model.Category
import com.samarth.aifinancecoach.domain.model.Transaction
import com.samarth.aifinancecoach.domain.model.TransactionType

object TransactionMapper {
    fun TransactionEntity.toDomain(): Transaction {
        return Transaction(
            id = id,
            amount = amount,
            type = TransactionType.valueOf(type),
            category = Category.valueOf(category),
            account = AccountType.valueOf(account),
            note = note,
            dateMillis = dateMillis,
            month = month,
            year = year,
            isRecurring = isRecurring,
            recurringIntervalDays = recurringIntervalDays ?: 30,
            tags = if (tags.isEmpty()) emptyList() else tags.split(",")
        )
    }

    fun Transaction.toEntity(): TransactionEntity {
        return TransactionEntity(
            id = id,
            amount = amount,
            type = type.name,
            category = category.name,
            account = account.name,
            note = note,
            dateMillis = dateMillis,
            month = month,
            year = year,
            isRecurring = isRecurring,
            recurringIntervalDays = recurringIntervalDays,
            tags = tags.joinToString(",")
        )
    }
}
