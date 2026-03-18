package com.samarth.aifinancecoach.domain.model

data class Budget(
    val id: Long = 0,
    val category: Category,
    val limitAmount: Double,
    val spentAmount: Double = 0.0,
    val month: Int,
    val year: Int
) {
    val remainingAmount: Double get() = limitAmount - spentAmount
    val percentageUsed: Float get() = 
        if (limitAmount > 0) ((spentAmount / limitAmount) * 100).toFloat().coerceIn(0f, 100f)
        else 0f
    val isExceeded: Boolean get() = spentAmount > limitAmount
    val isNearLimit: Boolean get() = percentageUsed >= 80f && !isExceeded
}
