package com.samarth.aifinancecoach.presentation.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────
// Brand — Midnight Green
// ─────────────────────────────────────────────

val MintGreen        = Color(0xFF00C896)
val MintGreenLight   = Color(0xFF33D4A8)
val MintGreenDark    = Color(0xFF009E78)

val OceanBlue        = Color(0xFF0096FF)
val OceanBlueLight   = Color(0xFF33ABFF)
val OceanBlueDark    = Color(0xFF0072CC)

val Amber            = Color(0xFFFFB340)
val AmberLight       = Color(0xFFFFCA70)
val AmberDark        = Color(0xFFCC8C28)

val ErrorRed         = Color(0xFFFF4D6A)
val ErrorRedLight    = Color(0xFFFF7A91)
val ErrorRedDark     = Color(0xFFCC2D47)

val SuccessGreen     = Color(0xFF27C93F)
val SuccessGreenLight= Color(0xFF55D96A)
val SuccessGreenDark = Color(0xFF1A9E2F)

// ─────────────────────────────────────────────
// Dark Theme Backgrounds
// ─────────────────────────────────────────────

val DarkBackground   = Color(0xFF0D1117)   // page background
val DarkSurface      = Color(0xFF1A1F2E)   // cards, bottom sheet
val DarkSurface2     = Color(0xFF151B27)   // elevated cards
val DarkSurfaceTint  = Color(0xFF00C896).copy(alpha = 0.08f)

// ─────────────────────────────────────────────
// Light Theme Backgrounds
// ─────────────────────────────────────────────

val LightBackground  = Color(0xFFF4F6F9)   // page background
val LightSurface     = Color(0xFFFFFFFF)   // cards
val LightSurface2    = Color(0xFFEAF9F5)   // tinted cards
val LightSurfaceTint = Color(0xFF00C896).copy(alpha = 0.08f)

// ─────────────────────────────────────────────
// Dark Theme Text
// ─────────────────────────────────────────────

val DarkTextPrimary   = Color(0xFFE8EDF5)
val DarkTextSecondary = Color(0xFF8B95A7)
val DarkTextHint      = Color(0xFF4A5568)

// ─────────────────────────────────────────────
// Light Theme Text
// ─────────────────────────────────────────────

val LightTextPrimary   = Color(0xFF0D1117)
val LightTextSecondary = Color(0xFF4A5568)
val LightTextHint      = Color(0xFF9AA5B4)

// ─────────────────────────────────────────────
// Dark Theme Borders
// ─────────────────────────────────────────────

val DarkBorder        = Color(0xFF252D3D)
val DarkBorderFocus   = Color(0xFF00C896).copy(alpha = 0.5f)

// ─────────────────────────────────────────────
// Light Theme Borders
// ─────────────────────────────────────────────

val LightBorder       = Color(0xFFE2E8F0)
val LightBorderFocus  = Color(0xFF00C896).copy(alpha = 0.6f)

// ─────────────────────────────────────────────
// Gradients — used as Brush in Compose
// ─────────────────────────────────────────────
// Usage: Box(modifier = Modifier.background(PrimaryGradient))

// Primary button / hero gradient
// Brush.horizontalGradient(listOf(MintGreen, OceanBlue))

// Expense indicator gradient
// Brush.horizontalGradient(listOf(ErrorRed, Amber))

// Income indicator gradient
// Brush.horizontalGradient(listOf(MintGreen, SuccessGreen))

// ─────────────────────────────────────────────
// Category Colors — for charts and chips
// ─────────────────────────────────────────────

val CategoryFood         = Color(0xFFFF6B6B)
val CategoryTransport    = Color(0xFF4ECDC4)
val CategoryShopping     = Color(0xFFFFE66D)
val CategoryBills        = Color(0xFF6C5CE7)
val CategoryEmi          = Color(0xFFA29BFE)
val CategoryHealth       = Color(0xFFFF7675)
val CategoryEntertainment= Color(0xFFFD79A8)
val CategoryInvestment   = Color(0xFF00B894)
val CategorySalary       = Color(0xFF00C896)
val CategoryFreelance    = Color(0xFF0096FF)
val CategoryGroceries    = Color(0xFFFFA502)
val CategoryEducation    = Color(0xFF1E90FF)
val CategoryTravel       = Color(0xFF2ED573)
val CategoryOther        = Color(0xFF8B95A7)