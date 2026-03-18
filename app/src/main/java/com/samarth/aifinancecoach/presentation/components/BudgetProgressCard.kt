package com.samarth.aifinancecoach.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.samarth.aifinancecoach.domain.model.Budget
import com.samarth.aifinancecoach.utils.CurrencyFormatter

@Composable
fun BudgetProgressCard(
    budget: Budget,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressColor = when {
        budget.isExceeded -> MaterialTheme.colorScheme.error
        budget.isNearLimit -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = budget.category.emoji, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = budget.category.label,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1
                )
            }

            Text(
                text = "${CurrencyFormatter.formatCompact(budget.spentAmount)} / ${CurrencyFormatter.formatCompact(budget.limitAmount)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LinearProgressIndicator(
                progress = { budget.percentageUsed / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Text(
                text = "${budget.percentageUsed.toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = progressColor,
                modifier = Modifier.align(Alignment.End),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
