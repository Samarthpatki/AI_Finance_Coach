package com.samarth.aifinancecoach.data.remote.api

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.samarth.aifinancecoach.domain.model.AiInsight
import com.samarth.aifinancecoach.domain.model.AiMessage
import com.samarth.aifinancecoach.domain.model.FinancialContext
import com.samarth.aifinancecoach.domain.model.MessageRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class GeminiDataSource @Inject constructor(
    @Named("geminiApiKey") private val apiKey: String
) {
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey,
        generationConfig = generationConfig {
            temperature = 0.7f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 1024
        },
        safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE)
        )
    )

    private val systemPrompt = """
        You are an expert personal finance coach for Indian users.
        You have access to the user's actual transaction data, budgets and spending patterns.
        Your personality:
        - Warm, encouraging and non-judgmental
        - Speak like a knowledgeable friend, not a formal advisor
        - Use Indian context (₹, lakhs, EMIs, UPI etc)
        - Be specific — always reference actual numbers from the data
        - Keep responses concise — max 3-4 paragraphs
        - Use bullet points for lists
        - Add 1 actionable tip at the end of every response

        You must NEVER:
        - Give generic advice not based on the user's data
        - Make up numbers not present in the context
        - Be preachy or repetitive
        - Suggest illegal or unethical financial activities.
    """.trimIndent()

    suspend fun sendMessage(
        userMessage: String,
        conversationHistory: List<AiMessage>,
        context: FinancialContext
    ): Flow<String> = flow {
        val prompt = buildFullPrompt(userMessage, conversationHistory, context)
        generativeModel.generateContentStream(prompt).collect { chunk ->
            chunk.text?.let { emit(it) }
        }
    }

    private fun buildFullPrompt(
        userMessage: String,
        conversationHistory: List<AiMessage>,
        context: FinancialContext
    ): String {
        val contextPrompt = buildContextPrompt(context)
        val historyPrompt = conversationHistory.joinToString("\n") { 
            "${it.role}: ${it.content}" 
        }
        
        return """
            $systemPrompt
            
            $contextPrompt
            
            CHAT HISTORY:
            $historyPrompt
            
            USER: $userMessage
            ASSISTANT:
        """.trimIndent()
    }

    private fun buildContextPrompt(context: FinancialContext): String {
        val monthName = SimpleDateFormat("MMMM", Locale.getDefault())
            .format(java.util.Calendar.getInstance().apply { set(java.util.Calendar.MONTH, context.currentMonth) }.time)
            
        val topCategories = context.topSpendingCategories.joinToString("\n") { 
            "- ${it.first.name}: ₹${it.second}" 
        }
        
        val recentTransactions = context.recentTransactions.joinToString("\n") {
            val date = SimpleDateFormat("dd MMM", Locale.getDefault()).format(it.dateMillis)
            "- [$date] ${it.category.name} ${it.note}: ₹${it.amount} (${it.type})"
        }
        
        val budgets = context.budgets.joinToString("\n") {
            val percent = if (it.limitAmount > 0) (it.spentAmount / it.limitAmount * 100).toInt() else 0
            "- ${it.category.name}: ₹${it.spentAmount} of ₹${it.limitAmount} ($percent% used)"
        }

        return """
            === FINANCIAL CONTEXT FOR ${context.userName} ===
            Month: $monthName ${context.currentYear}
            Monthly Income: ₹${context.monthlyIncome}
            Monthly Expenses: ₹${context.monthlyExpense}
            Net Savings: ₹${context.monthlyIncome - context.monthlyExpense}
            Savings Rate: ${String.format("%.1f", context.savingsRate)}%

            TOP SPENDING CATEGORIES:
            $topCategories

            RECENT TRANSACTIONS (last 30):
            $recentTransactions

            ACTIVE BUDGETS:
            $budgets
            === END CONTEXT ===
        """.trimIndent()
    }

    suspend fun generateInsights(context: FinancialContext): List<AiInsight> {
        val prompt = """
            ${buildContextPrompt(context)}
            
            Based on the financial data above, generate 3-5 personalized financial insights.
            For each insight, provide:
            1. A short title
            2. A descriptive message
            3. Type (one of: OVERSPENDING, SAVING_OPPORTUNITY, UNUSUAL_TRANSACTION, BUDGET_ALERT, MONTHLY_SUMMARY, POSITIVE_TREND)
            
            Return the response in a structured format that I can parse. 
            Format each insight as:
            [TITLE] Title here
            [MESSAGE] Message here
            [TYPE] Type here
            ---
        """.trimIndent()

        val response = generativeModel.generateContent(prompt)
        return parseInsights(response.text ?: "")
    }

    private fun parseInsights(text: String): List<AiInsight> {
        val insights = mutableListOf<AiInsight>()
        val blocks = text.split("---")
        for (block in blocks) {
            if (block.isBlank()) continue
            val title = block.substringAfter("[TITLE]").substringBefore("\n").trim()
            val message = block.substringAfter("[MESSAGE]").substringBefore("\n").trim()
            val typeStr = block.substringAfter("[TYPE]").substringBefore("\n").trim()
            
            val type = try {
                com.samarth.aifinancecoach.domain.model.InsightType.valueOf(typeStr)
            } catch (e: Exception) {
                com.samarth.aifinancecoach.domain.model.InsightType.MONTHLY_SUMMARY
            }
            
            insights.add(
                AiInsight(
                    id = System.currentTimeMillis() + insights.size,
                    title = title,
                    description = message,
                    type = type,
                    timestamp = System.currentTimeMillis(),
                    isRead = false
                )
            )
        }
        return insights
    }

    suspend fun generateMonthlyReport(context: FinancialContext): String {
        val prompt = """
            ${buildContextPrompt(context)}
            
            Generate a comprehensive monthly financial report for ${context.userName} for ${context.currentMonth}/${context.currentYear}.
            Include sections for:
            - ## Financial Health Overview
            - ## Key Spending Insights
            - ## Budget Performance
            - ## Actionable Recommendations
            
            Use markdown formatting (## for headers, - for bullets, ** for bold).
            Be encouraging but honest about their spending habits.
        """.trimIndent()

        val response = generativeModel.generateContent(prompt)
        return response.text ?: ""
    }
}
