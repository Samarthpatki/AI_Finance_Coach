package com.samarth.aifinancecoach.domain.usecase.budget

import com.samarth.aifinancecoach.domain.model.Category
import com.samarth.aifinancecoach.domain.model.TransactionType
import com.samarth.aifinancecoach.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetBudgetSuggestionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(
        category: Category,
        currentMonth: Int,
        currentYear: Int
    ): Double? {
        val expenseTotals = mutableListOf<Double>()
        
        // Go back 3 months
        for (i in 1..3) {
            var targetMonth = currentMonth - i
            var targetYear = currentYear
            if (targetMonth < 0) {
                targetMonth += 12
                targetYear -= 1
            }
            
            val transactions = transactionRepository.getTransactionsByMonth(targetMonth, targetYear).first()
            val categoryTotal = transactions
                .filter { it.category == category && it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }
            
            if (categoryTotal > 0) {
                expenseTotals.add(categoryTotal)
            }
        }

        if (expenseTotals.isEmpty()) return null

        val average = expenseTotals.average()
        // Return average with a 10% buffer
        return average * 1.1
    }
}
