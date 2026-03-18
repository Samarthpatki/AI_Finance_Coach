package com.samarth.aifinancecoach.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    // Chips, badges, small tags
    extraSmall = RoundedCornerShape(4.dp),

    // Input fields, small cards
    small = RoundedCornerShape(8.dp),

    // Standard cards, dialogs
    medium = RoundedCornerShape(12.dp),

    // Bottom sheets, large cards
    large = RoundedCornerShape(16.dp),

    // FABs, full rounded buttons
    extraLarge = RoundedCornerShape(24.dp)
)