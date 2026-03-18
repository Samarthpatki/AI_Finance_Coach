package com.samarth.aifinancecoach.domain.model

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String,
    val monthlyIncome: Double,
    val currency: String = "INR",
    val currencySymbol: String = "₹",
    val profileSetupComplete: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
