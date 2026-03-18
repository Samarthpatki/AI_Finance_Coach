package com.samarth.aifinancecoach.presentation.ai.chat

import com.samarth.aifinancecoach.domain.model.AiMessage
import java.util.Calendar

data class AiChatState(
    val messages: List<AiMessage> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val streamingMessageId: Long? = null,
    val streamingContent: String = "",
    val error: String? = null,
    val currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    val currentYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val showClearDialog: Boolean = false
)
