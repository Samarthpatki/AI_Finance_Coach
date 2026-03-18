package com.samarth.aifinancecoach.domain.model

import androidx.compose.ui.graphics.Color
import com.samarth.aifinancecoach.presentation.theme.*

enum class Category(val label: String, val emoji: String, val color: Color) {
    FOOD("Food & Dining", "🍔", CategoryFood),
    TRANSPORT("Transport", "🚗", CategoryTransport),
    SHOPPING("Shopping", "🛍️", CategoryShopping),
    BILLS("Bills & Utilities", "💡", CategoryBills),
    EMI("EMI & Loans", "🏦", CategoryEmi),
    HEALTH("Health & Medical", "💊", CategoryHealth),
    ENTERTAINMENT("Entertainment", "🎬", CategoryEntertainment),
    INVESTMENT("Investment", "📈", CategoryInvestment),
    SALARY("Salary", "💰", CategorySalary),
    FREELANCE("Freelance", "💻", CategoryFreelance),
    GROCERIES("Groceries", "🛒", CategoryGroceries),
    EDUCATION("Education", "📚", CategoryEducation),
    TRAVEL("Travel", "✈️", CategoryTravel),
    OTHER("Other", "📦", CategoryOther)
}
