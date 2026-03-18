package com.samarth.aifinancecoach.domain.usecase.transaction

import com.samarth.aifinancecoach.domain.model.Transaction
import com.samarth.aifinancecoach.domain.repository.TransactionRepository
import javax.inject.Inject

class UpdateTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        repository.updateTransaction(transaction)
    }
}
