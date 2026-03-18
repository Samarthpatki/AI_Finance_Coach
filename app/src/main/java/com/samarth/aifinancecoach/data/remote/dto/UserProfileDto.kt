package com.samarth.aifinancecoach.data.remote.dto

import androidx.annotation.Keep

@Keep
data class UserProfileDto(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val monthlyIncome: Double? = null,
    val currency: String? = null,
    val currencySymbol: String? = null,
    val profileSetupComplete: Boolean? = null,
    val createdAt: Long? = null
)
