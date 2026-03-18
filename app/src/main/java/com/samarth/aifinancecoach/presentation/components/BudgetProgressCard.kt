package com.samarth.aifinancecoach.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samarth.aifinancecoach.R
import com.samarth.aifinancecoach.domain.model.Budget
import com.samarth.aifinancecoach.presentation.theme.DmSansFontFamily
import com.samarth.aifinancecoach.presentation.theme.ErrorRed
import com.samarth.aifinancecoach.presentation.theme.SoraFontFamily
import com.samarth.aifinancecoach.presentation.theme.SuccessGreen
import com.samarth.aifinancecoach.utils.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetCard(
    budget: Budget,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val alignment = Alignment.CenterEnd
            val color = ErrorRed.copy(alpha = 0.8f)

            Box(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        enableDismissFromStartToEnd = false,
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // TOP ROW
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = budget.category.emoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = budget.category.label,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontFamily = SoraFontFamily,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }

                    StatusChip(budget)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // AMOUNT ROW
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            R.string.budget_spent_of,
                            CurrencyFormatter.format(budget.spentAmount),
                            CurrencyFormatter.format(budget.limitAmount)
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    val remainingText = if (budget.isExceeded) {
                        stringResource(R.string.budget_exceeded, CurrencyFormatter.format(budget.spentAmount - budget.limitAmount))
                    } else {
                        stringResource(R.string.budget_remaining_amount, CurrencyFormatter.format(budget.limitAmount - budget.spentAmount))
                    }

                    Text(
                        text = remainingText,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (budget.isExceeded) ErrorRed else SuccessGreen
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // PROGRESS BAR
                val animatedProgress by animateFloatAsState(
                    targetValue = (budget.percentageUsed / 100f).coerceIn(0f, 1f),
                    animationSpec = tween(800),
                    label = "BudgetProgress"
                )

                val progressBarColor = when {
                    budget.isExceeded -> ErrorRed
                    budget.isNearLimit -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                }

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = progressBarColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // BOTTOM ROW
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${budget.percentageUsed.toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Edit",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = DmSansFontFamily
                        ),
                        modifier = Modifier.clickable { onClick() }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusChip(budget: Budget) {
    val (backgroundColor, textColor, label) = when {
        budget.isExceeded -> Triple(
            ErrorRed.copy(alpha = 0.15f),
            ErrorRed,
            stringResource(R.string.budget_exceeded_label)
        )
        budget.isNearLimit -> Triple(
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.tertiary,
            stringResource(R.string.budget_near_limit)
        )
        else -> Triple(
            MaterialTheme.colorScheme.surface,
            SuccessGreen,
            stringResource(R.string.budget_on_track)
        )
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp),
        border = if (!budget.isExceeded && !budget.isNearLimit) 
            androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen) 
            else null
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = DmSansFontFamily,
                color = textColor
            )
        )
    }
}

@Composable
fun BudgetProgressCard(
    budget: Budget,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressColor = when {
        budget.isExceeded -> ErrorRed
        budget.isNearLimit -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "${CurrencyFormatter.formatCompact(budget.spentAmount)} / ${CurrencyFormatter.formatCompact(budget.limitAmount)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            LinearProgressIndicator(
                progress = { (budget.percentageUsed / 100f).coerceIn(0f, 1f) },
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
                fontFamily = SoraFontFamily
            )
        }
    }
}
