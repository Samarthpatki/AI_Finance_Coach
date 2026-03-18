package com.samarth.aifinancecoach.presentation.ai.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samarth.aifinancecoach.domain.model.AiMessage
import com.samarth.aifinancecoach.domain.model.MessageRole
import com.samarth.aifinancecoach.domain.usecase.ai.ClearChatHistoryUseCase
import com.samarth.aifinancecoach.domain.usecase.ai.GetChatHistoryUseCase
import com.samarth.aifinancecoach.domain.usecase.ai.SendAiMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val sendAiMessageUseCase: SendAiMessageUseCase,
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val clearChatHistoryUseCase: ClearChatHistoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AiChatState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getChatHistoryUseCase().collect { history ->
                _state.update { it.copy(messages = history) }
            }
        }
    }

    fun onInputChanged(text: String) {
        if (text.length <= 500) {
            _state.update { it.copy(inputText = text) }
        }
    }

    fun onSendMessage() {
        val currentInput = _state.value.inputText
        if (currentInput.isBlank() || _state.value.isLoading) return

        val userMessage = AiMessage(
            role = MessageRole.USER,
            content = currentInput
        )

        // Optimistic update
        val updatedMessages = _state.value.messages + userMessage
        val assistantPlaceholder = AiMessage(
            role = MessageRole.ASSISTANT,
            content = "",
            isLoading = true
        )

        _state.update {
            it.copy(
                messages = updatedMessages + assistantPlaceholder,
                inputText = "",
                isLoading = true,
                streamingContent = ""
            )
        }

        viewModelScope.launch {
            try {
                // Get last 10 messages for context
                val history = updatedMessages.takeLast(10)
                
                var fullContent = ""
                sendAiMessageUseCase(
                    userMessage = currentInput,
                    conversationHistory = history,
                    month = _state.value.currentMonth,
                    year = _state.value.currentYear
                ).collect { token ->
                    fullContent += token
                    _state.update { state ->
                        val newMessages = state.messages.toMutableList()
                        val lastIdx = newMessages.lastIndex
                        if (lastIdx >= 0 && newMessages[lastIdx].role == MessageRole.ASSISTANT) {
                            newMessages[lastIdx] = newMessages[lastIdx].copy(
                                content = fullContent,
                                isLoading = true
                            )
                        }
                        state.copy(
                            messages = newMessages,
                            streamingContent = fullContent
                        )
                    }
                }
                
                // Finalize message
                _state.update { state ->
                    val newMessages = state.messages.toMutableList()
                    val lastIdx = newMessages.lastIndex
                    if (lastIdx >= 0 && newMessages[lastIdx].role == MessageRole.ASSISTANT) {
                        newMessages[lastIdx] = newMessages[lastIdx].copy(isLoading = false)
                    }
                    state.copy(messages = newMessages, isLoading = false)
                }
            } catch (e: Exception) {
                _state.update { state ->
                    val newMessages = state.messages.toMutableList()
                    val lastIdx = newMessages.lastIndex
                    if (lastIdx >= 0 && newMessages[lastIdx].role == MessageRole.ASSISTANT) {
                        newMessages[lastIdx] = newMessages[lastIdx].copy(
                            isError = true,
                            isLoading = false
                        )
                    }
                    state.copy(messages = newMessages, isLoading = false)
                }
            }
        }
    }

    fun onQuickPromptSelected(prompt: String) {
        _state.update { it.copy(inputText = prompt) }
        onSendMessage()
    }

    fun onClearChatClicked() {
        _state.update { it.copy(showClearDialog = true) }
    }

    fun onConfirmClear() {
        viewModelScope.launch {
            clearChatHistoryUseCase()
            _state.update { it.copy(showClearDialog = false, messages = emptyList()) }
        }
    }

    fun onDismissClear() {
        _state.update { it.copy(showClearDialog = false) }
    }

    fun retryLastMessage() {
        val lastUserMessage = _state.value.messages.lastOrNull { it.role == MessageRole.USER }
        if (lastUserMessage != null) {
            val cleanedMessages = _state.value.messages.filter { !it.isError }
            _state.update { it.copy(messages = cleanedMessages, inputText = lastUserMessage.content) }
            onSendMessage()
        }
    }
}
