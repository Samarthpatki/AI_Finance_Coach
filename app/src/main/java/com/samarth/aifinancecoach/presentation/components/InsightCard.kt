package com.samarth.aifinancecoach.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.samarth.aifinancecoach.domain.model.AiInsight
import com.samarth.aifinancecoach.domain.model.InsightType
import com.samarth.aifinancecoach.presentation.theme.DmSansFontFamily
import com.samarth.aifinancecoach.presentation.theme.SoraFontFamily
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun InsightCard(
    insight: AiInsight,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when (insight.type) {
        InsightType.OVERSPENDING -> MaterialTheme.colorScheme.error
        InsightType.SAVING_OPPORTUNITY -> MaterialTheme.colorScheme.primary
        InsightType.UNUSUAL_TRANSACTION -> MaterialTheme.colorScheme.tertiary
        InsightType.BUDGET_ALERT -> MaterialTheme.colorScheme.tertiary
        InsightType.MONTHLY_SUMMARY -> MaterialTheme.colorScheme.secondary
        InsightType.POSITIVE_TREND -> Color(0xFF27C93F)
    }

    val icon = when (insight.type) {
        InsightType.OVERSPENDING -> Icons.AutoMirrored.Rounded.TrendingDown
        InsightType.SAVING_OPPORTUNITY -> Icons.Rounded.Savings
        InsightType.UNUSUAL_TRANSACTION -> Icons.Rounded.Warning
        InsightType.BUDGET_ALERT -> Icons.Rounded.NotificationsActive
        InsightType.MONTHLY_SUMMARY -> Icons.Rounded.Summarize
        InsightType.POSITIVE_TREND -> Icons.AutoMirrored.Rounded.TrendingUp
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .alpha(if (insight.isRead) 0.6f else 1f)
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = onClick
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Left Accent Border
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(borderColor)
            )

            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = borderColor
                    )
                    Text(
                        text = insight.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = SoraFontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!insight.isRead) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }

                Text(
                    text = insight.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = DmSansFontFamily
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(insight.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    insight.relatedCategory?.let { category ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = category.color.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${category.emoji} ${category.name}",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
