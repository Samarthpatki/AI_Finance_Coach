package com.samarth.aifinancecoach.presentation.ai.report

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.samarth.aifinancecoach.R
import com.samarth.aifinancecoach.presentation.components.MonthSelector
import com.samarth.aifinancecoach.presentation.theme.DmSansFontFamily
import com.samarth.aifinancecoach.presentation.theme.SoraFontFamily
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiReportScreen(
    viewModel: AiReportViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.ai_report_title),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = SoraFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, state.reportContent)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        },
                        enabled = state.reportContent.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MonthSelector(
                month = state.currentMonth,
                year = state.currentYear,
                onNextClick = {},
                onPreviousClick = {},
//                onMonthYearSelected = viewModel::onMonthChanged
            )

            if (state.isLoading) {
                LoadingReportState()
            } else if (state.reportContent.isEmpty()) {
                EmptyReportState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        ReportHeroCard(state)
                    }
                    item {
                        ReportContent(state.reportContent)
                    }
                }
            }
        }
    }
}

@Composable
fun ReportHeroCard(state: AiReportState) {
    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(
        Calendar.getInstance().apply { 
            set(Calendar.MONTH, state.currentMonth)
            set(Calendar.YEAR, state.currentYear)
        }.time
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$monthName ${state.currentYear}",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = SoraFontFamily,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "AI Financial Report",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ReportStatItem("Income", "₹${state.monthlyAnalytics?.totalIncome ?: 0}")
                ReportStatItem("Savings", "${state.monthlyAnalytics?.savingsRate?.toInt() ?: 0}%")
                ReportStatItem("Expense", "₹${state.monthlyAnalytics?.totalExpense ?: 0}")
            }
        }
    }
}

@Composable
fun ReportStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun ReportContent(content: String) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        content.split("\n").forEach { line ->
            when {
                line.startsWith("##") -> {
                    Text(
                        text = line.replace("##", "").trim(),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = SoraFontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                line.startsWith("-") -> {
                    Row(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = line.substring(1).trim(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = DmSansFontFamily
                            )
                        )
                    }
                }
                else -> {
                    if (line.isNotBlank()) {
                        Text(
                            text = line.trim(),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = DmSansFontFamily
                            ),
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingReportState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.ai_report_generating),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EmptyReportState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.ai_report_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
