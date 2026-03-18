package com.samarth.aifinancecoach.data.mapper

import com.samarth.aifinancecoach.data.local.entity.BudgetEntity
import com.samarth.aifinancecoach.domain.model.Budget
import com.samarth.aifinancecoach.domain.model.Category

object BudgetMapper {
    fun BudgetEntity.toDomain(): Budget {
        return Budget(
            id = id,
            category = Category.valueOf(category),
            limitAmount = limitAmount,
            spentAmount = spentAmount,
            month = month,
            year = year
        )
    }

    fun Budget.toEntity(): BudgetEntity {
        return BudgetEntity(
            id = id,
            category = category.name,
            limitAmount = limitAmount,
            spentAmount = spentAmount,
            month = month,
            year = year
        )
    }
}
