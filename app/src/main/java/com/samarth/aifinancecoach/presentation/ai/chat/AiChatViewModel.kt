package com.samarth.aifinancecoach.presentation.ai.chat

import android.util.Log
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

    private val TAG = "AiChatViewModel"
    private val _state = MutableStateFlow(AiChatState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getChatHistoryUseCase().collect { history ->
                Log.d(TAG, "Chat history updated: ${history.size} messages")
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
        if (currentInput.isBlank() || _state.value.isLoading) {
            Log.d(TAG, "Cannot send message: input is blank or already loading")
            return
        }

        Log.d(TAG, "User sending message: $currentInput")

        // Grab current history BEFORE adding new messages for LLM context
        val contextHistory = _state.value.messages.takeLast(10)

        _state.update {
            it.copy(
                inputText = "",
                isLoading = true,
                error = null,
                streamingContent = ""
            )
        }

        viewModelScope.launch {
            try {
                var fullContent = ""
                Log.d(TAG, "Invoking sendAiMessageUseCase")
                sendAiMessageUseCase(
                    userMessage = currentInput,
                    conversationHistory = contextHistory,
                    month = _state.value.currentMonth,
                    year = _state.value.currentYear
                ).catch { e ->
                    Log.e(TAG, "Error in sendAiMessageUseCase: ${e.message}", e)
                    _state.update { it.copy(isLoading = false, error = e.message) }
                }.collect { token ->
                    fullContent += token
                    Log.v(TAG, "Received token, full content length: ${fullContent.length}")
                    _state.update { it.copy(streamingContent = fullContent) }
                }
                
                Log.d(TAG, "Message streaming finished successfully")
                _state.update { it.copy(isLoading = false, streamingContent = "") }
            } catch (e: Exception) {
                Log.e(TAG, "Exception caught in onSendMessage: ${e.message}", e)
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onQuickPromptSelected(prompt: String) {
        Log.d(TAG, "Quick prompt selected: $prompt")
        _state.update { it.copy(inputText = prompt) }
        onSendMessage()
    }

    fun onClearChatClicked() {
        _state.update { it.copy(showClearDialog = true) }
    }

    fun onConfirmClear() {
        viewModelScope.launch {
            Log.d(TAG, "Clearing chat history")
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
            Log.d(TAG, "Retrying last user message: ${lastUserMessage.content}")
            _state.update { it.copy(inputText = lastUserMessage.content) }
            onSendMessage()
        }
    }
}
