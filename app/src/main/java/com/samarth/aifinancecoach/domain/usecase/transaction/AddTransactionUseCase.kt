package com.samarth.aifinancecoach.domain.usecase.transaction

import com.samarth.aifinancecoach.domain.model.Transaction
import com.samarth.aifinancecoach.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Long {
        return repository.insertTransaction(transaction)
    }
}
