package com.samarth.aifinancecoach.domain.model

data class AiMessage(
    val id: Long = 0,
    val role: MessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false,
    val isError: Boolean = false
)

enum class MessageRole {
    USER, ASSISTANT
}
