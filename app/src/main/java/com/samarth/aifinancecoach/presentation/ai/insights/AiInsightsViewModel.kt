package com.samarth.aifinancecoach.presentation.ai.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samarth.aifinancecoach.domain.usecase.ai.GenerateInsightsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiInsightsViewModel @Inject constructor(
    private val generateInsightsUseCase: GenerateInsightsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AiInsightsState())
    val state = _state.asStateFlow()

    init {
        generateInsights()
    }

    fun generateInsights() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val insights = generateInsightsUseCase(
                    month = _state.value.currentMonth,
                    year = _state.value.currentYear
                )
                _state.update { it.copy(insights = insights, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun onRefresh() {
        _state.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            try {
                val insights = generateInsightsUseCase(
                    month = _state.value.currentMonth,
                    year = _state.value.currentYear
                )
                _state.update { it.copy(insights = insights, isRefreshing = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isRefreshing = false) }
            }
        }
    }

    fun onMarkAsRead(insightId: Long) {
        _state.update { state ->
            val updatedInsights = state.insights.map {
                if (it.id == insightId) it.copy(isRead = true) else it
            }
            state.copy(insights = updatedInsights)
        }
    }
}
