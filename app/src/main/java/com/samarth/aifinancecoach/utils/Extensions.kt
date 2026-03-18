package com.samarth.aifinancecoach.utils

fun String.isValidName(): Boolean = this.trim().length >= 2

fun String.isValidIncome(): Boolean =
    this.toDoubleOrNull()?.let { it > 0 } ?: false

fun Double.formatAsCurrency(symbol: String): String =
    "$symbol${String.format("%,.0f", this)}"
