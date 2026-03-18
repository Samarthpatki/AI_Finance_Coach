package com.samarth.aifinancecoach.domain.usecase.transaction

import com.samarth.aifinancecoach.domain.model.Transaction
import com.samarth.aifinancecoach.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionsByMonthUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    operator fun invoke(month: Int, year: Int): Flow<List<Transaction>> =
        repository.getTransactionsByMonth(month, year)
}
