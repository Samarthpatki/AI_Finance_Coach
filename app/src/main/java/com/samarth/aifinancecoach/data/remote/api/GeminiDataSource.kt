package com.samarth.aifinancecoach.data.remote.api

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.generationConfig
import com.samarth.aifinancecoach.domain.model.AiInsight
import com.samarth.aifinancecoach.domain.model.AiMessage
import com.samarth.aifinancecoach.domain.model.FinancialContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class GeminiDataSource @Inject constructor(
    @Named("geminiApiKey") private val apiKey: String
) {
    private val TAG = "GeminiDataSource"

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey,
        generationConfig = generationConfig {
            temperature = 0.7f
            topK = 40
            topP = 0.95f
            maxOutputTokens = 2048
        },
        safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE)
        )
    )

    private val systemPrompt = """
        You are "AI Coach", an expert personal finance coach for Indian users.
        You have access to the user's actual transaction data, budgets and spending patterns.
        
        Your Goal: Help users save more, spend wisely, and reach their financial goals.
        
        Your personality:
        - Warm, encouraging and non-judgmental.
        - Speak like a knowledgeable friend (using terms like 'bro', 'buddy', or just friendly casual tone).
        - Use Indian context exclusively (₹, lakhs, EMIs, UPI, SIPs, FD, Gold).
        - Be highly specific — always reference actual numbers, dates, and category names from the provided context.
        - Keep responses concise but high-value — max 3 short paragraphs.
        - Use Markdown for formatting (bold for numbers, bullet points for lists).
        - Always end with exactly 1 actionable "Coach's Tip" relevant to the conversation.

        You must NEVER:
        - Give generic advice like "save 20%" unless it's backed by their actual data.
        - Make up transactions or budgets that don't exist in the context.
        - Mention you are an AI or a large language model.
    """.trimIndent()

    suspend fun sendMessage(
        userMessage: String,
        conversationHistory: List<AiMessage>,
        context: FinancialContext
    ): Flow<String> = flow {
        val prompt = buildFullPrompt(userMessage, conversationHistory, context)
        Log.d(TAG, "Sending prompt to Gemini: $prompt")
        
        generativeModel.generateContentStream(prompt)
            .onStart { Log.d(TAG, "Gemini stream started") }
            .catch { e -> 
                Log.e(TAG, "Error in Gemini stream: ${e.message}", e)
                throw e
            }
            .onCompletion { Log.d(TAG, "Gemini stream completed") }
            .collect { chunk ->
                chunk.text?.let { 
                    Log.d(TAG, "Received chunk: $it")
                    emit(it) 
                }
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
            AI COACH:
        """.trimIndent()
    }

    private fun buildContextPrompt(context: FinancialContext): String {
        val topCategories = context.topSpendingCategories.joinToString("\n") { 
            "- ${it.first.name}: ${context.currencySymbol}${it.second}" 
        }
        
        val recentTransactions = context.recentTransactions.joinToString("\n") {
            val date = SimpleDateFormat("dd MMM", Locale.getDefault()).format(it.dateMillis)
            "- [$date] ${it.category.name}: ${context.currencySymbol}${it.amount} (${it.note})"
        }
        
        val budgets = context.budgets.joinToString("\n") {
            val percent = if (it.limitAmount > 0) (it.spentAmount / it.limitAmount * 100).toInt() else 0
            "- ${it.category.name}: Spent ${context.currencySymbol}${it.spentAmount} of ${context.currencySymbol}${it.limitAmount} ($percent%)"
        }

        return """
            === USER FINANCIAL DATA ===
            User: ${context.userName}
            Monthly Income: ${context.currencySymbol}${context.monthlyIncome}
            This Month's Total Expenses: ${context.currencySymbol}${context.monthlyExpense}
            Current Savings: ${context.currencySymbol}${context.monthlyIncome - context.monthlyExpense}
            Savings Rate: ${String.format("%.1f", context.savingsRate)}%

            TOP SPENDING CATEGORIES:
            $topCategories

            RECENT TRANSACTIONS:
            $recentTransactions

            ACTIVE BUDGETS:
            $budgets
            === END DATA ===
        """.trimIndent()
    }

    suspend fun generateInsights(context: FinancialContext): List<AiInsight> {
        val prompt = """
            ${buildContextPrompt(context)}
            
            As a finance coach, look at the data above and generate 3 personalized financial insights.
            Format each insight as:
            [TITLE] <short title>
            [MESSAGE] <1-2 sentence insight referencing specific data>
            [TYPE] <OVERSPENDING | SAVING_OPPORTUNITY | UNUSUAL_TRANSACTION | BUDGET_ALERT | MONTHLY_SUMMARY | POSITIVE_TREND>
            ---
        """.trimIndent()

        Log.d(TAG, "Generating insights with prompt: $prompt")
        return try {
            val response = generativeModel.generateContent(prompt)
            Log.d(TAG, "Insights response: ${response.text}")
            parseInsights(response.text ?: "")
        } catch (e: Exception) {
            Log.e(TAG, "Error generating insights: ${e.message}", e)
            emptyList()
        }
    }

    private fun parseInsights(text: String): List<AiInsight> {
        val insights = mutableListOf<AiInsight>()
        val blocks = text.split("---")
        for (block in blocks) {
            if (block.isBlank() || !block.contains("[TITLE]")) continue
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
            
            Generate a comprehensive monthly financial report for ${context.userName}.
            Use the data to create these sections:
            - ## Monthly Health Checkup (How did they do overall?)
            - ## Where the money went (Highlight top categories)
            - ## Budget Scorecard
            - ## Coach's Game Plan (Specific actionable steps for next month)
            
            Use markdown. Be supportive.
        """.trimIndent()

        Log.d(TAG, "Generating monthly report...")
        return try {
            val response = generativeModel.generateContent(prompt)
            response.text ?: ""
        } catch (e: Exception) {
            Log.e(TAG, "Error generating monthly report: ${e.message}", e)
            "Error generating report. Please try again later."
        }
    }
}
