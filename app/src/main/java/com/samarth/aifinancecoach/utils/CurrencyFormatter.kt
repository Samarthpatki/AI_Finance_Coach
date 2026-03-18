package com.samarth.aifinancecoach.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    fun format(amount: Double, symbol: String = "₹"): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        val formatted = format.format(amount)
        // NumberFormat.getCurrencyInstance adds the currency symbol based on locale,
        // but to be safe and use the provided symbol:
        return formatted.replace(format.currency?.symbol ?: "₹", symbol)
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
