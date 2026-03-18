package com.samarth.aifinancecoach.data.mapper

import com.samarth.aifinancecoach.data.remote.dto.UserProfileDto
import com.samarth.aifinancecoach.domain.model.UserProfile

object UserProfileMapper {
    fun UserProfile.toDto(): UserProfileDto {
        return UserProfileDto(
            id = id,
            name = name,
            email = email,
            photoUrl = photoUrl,
            monthlyIncome = monthlyIncome,
            currency = currency,
            currencySymbol = currencySymbol,
            profileSetupComplete = profileSetupComplete,
            createdAt = createdAt
        )
    }

    fun UserProfileDto.toDomain(): UserProfile {
        return UserProfile(
            id = id ?: "",
            name = name ?: "",
            email = email ?: "",
            photoUrl = photoUrl ?: "",
            monthlyIncome = monthlyIncome ?: 0.0,
            currency = currency ?: "INR",
            currencySymbol = currencySymbol ?: "₹",
            profileSetupComplete = profileSetupComplete ?: false,
            createdAt = createdAt ?: System.currentTimeMillis()
        )
    }
}
