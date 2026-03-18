package com.samarth.aifinancecoach.utils

import com.samarth.aifinancecoach.domain.model.TransactionType
import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    fun format(amount: Double, symbol: String = "₹"): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        val formatted = format.format(amount)
        return formatted.replace(format.currency?.symbol ?: "₹", symbol)
    }

    fun formatWithSign(amount: Double, type: TransactionType, symbol: String = "₹"): String {
        val sign = if (type == TransactionType.INCOME) "+" else "-"
        return "$sign${format(amount, symbol)}"
    }

    fun formatCompact(amount: Double, symbol: String = "₹"): String {
        return when {
            amount >= 10000000 -> "${symbol}${(amount / 10000000).format(1)}Cr"
            amount >= 100000 -> "${symbol}${(amount / 100000).format(1)}L"
            amount >= 1000 -> "${symbol}${(amount / 1000).format(1)}K"
            else -> "${symbol}${amount.toInt()}"
        }
    }

    private fun Double.format(digits: Int) = "%.${digits}f".format(this)
}
