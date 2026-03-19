package com.samarth.aifinancecoach.presentation.ai.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samarth.aifinancecoach.domain.usecase.ai.GenerateMonthlyReportUseCase
import com.samarth.aifinancecoach.domain.usecase.analytics.GetMonthlyAnalyticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiReportViewModel @Inject constructor(
    private val generateMonthlyReportUseCase: GenerateMonthlyReportUseCase,
    private val getMonthlyAnalyticsUseCase: GetMonthlyAnalyticsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AiReportState())
    val state = _state.asStateFlow()

    init {
        generateReport()
    }

    fun generateReport() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                // Since GetMonthlyAnalyticsUseCase now returns a Flow, 
                // we use .first() to get the current snapshot for the report generation.
                val analytics = getMonthlyAnalyticsUseCase(
                    _state.value.currentMonth,
                    _state.value.currentYear
                ).first()
                
                val report = generateMonthlyReportUseCase(
                    _state.value.currentMonth,
                    _state.value.currentYear
                )

                _state.update { it.copy(
                    reportContent = report,
                    monthlyAnalytics = analytics,
                    isLoading = false
                ) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun onMonthChanged(month: Int, year: Int) {
        _state.update { it.copy(currentMonth = month, currentYear = year) }
        generateReport()
    }
}
