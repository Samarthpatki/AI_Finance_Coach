package com.samarth.aifinancecoach.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.samarth.aifinancecoach.R
import com.samarth.aifinancecoach.presentation.theme.SoraFontFamily
import com.samarth.aifinancecoach.utils.CurrencyFormatter

@Composable
fun SummaryCard(
    totalIncome: Double,
    totalExpense: Double,
    monthName: String,
    year: Int,
    modifier: Modifier = Modifier
) {
    val netSavings = totalIncome - totalExpense
    val savingsRate = if (totalIncome > 0) (netSavings / totalIncome) * 100 else 0.0
    val netSavingsColor = if (netSavings >= 0) Color(0xFF27C93F) else Color(0xFFFF4D6A)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Month & Year
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_this_month),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$monthName $year",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Net Savings
                Column {
                    Text(
                        text = CurrencyFormatter.format(netSavings),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontFamily = SoraFontFamily,
                            color = netSavingsColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = stringResource(R.string.dashboard_total_balance),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Income & Expenses
                Row(modifier = Modifier.fillMaxWidth()) {
                    SummaryItem(
                        label = stringResource(R.string.dashboard_income),
                        amount = totalIncome,
                        color = Color(0xFF27C93F), // SuccessGreen
                        modifier = Modifier.weight(1f)
                    )
                    SummaryItem(
                        label = stringResource(R.string.dashboard_expense),
                        amount = totalExpense,
                        color = Color(0xFFFF4D6A), // ErrorRed
                        modifier = Modifier.weight(1f),
                        isEnd = true
                    )
                }

                // Savings Rate
                Text(
                    text = "${stringResource(R.string.dashboard_savings_rate)}: ${"%.1f".format(savingsRate)}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    amount: Double,
    color: Color,
    modifier: Modifier = Modifier,
    isEnd: Boolean = false
) {
    Column(
        modifier = modifier,
        horizontalAlignment = if (isEnd) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = CurrencyFormatter.format(amount),
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
