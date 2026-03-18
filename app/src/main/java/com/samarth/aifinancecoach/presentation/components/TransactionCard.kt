package com.samarth.aifinancecoach.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.samarth.aifinancecoach.domain.model.Transaction
import com.samarth.aifinancecoach.domain.model.TransactionType
import com.samarth.aifinancecoach.presentation.theme.ErrorRed
import com.samarth.aifinancecoach.presentation.theme.SoraFontFamily
import com.samarth.aifinancecoach.presentation.theme.SuccessGreen
import com.samarth.aifinancecoach.utils.CurrencyFormatter
import com.samarth.aifinancecoach.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionCard(
    transaction: Transaction,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    showTime: Boolean = true
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
        enableDismissFromStartToEnd = false,
        backgroundContent = {
//            val color = when (dismissState.dismissDirection) {
//                SwipeToDismissBoxDirection.EndToStart -> ErrorRed
//                else -> Color.Transparent
//            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
//                    .background(color)
                    .background(Color.Transparent)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(transaction.category.color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = transaction.category.emoji,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Middle Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.category.label,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = transaction.note.ifBlank { transaction.account.name },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Right Info
            Column(horizontalAlignment = Alignment.End) {
                val color = if (transaction.type == TransactionType.INCOME) SuccessGreen else ErrorRed
                Text(
                    text = CurrencyFormatter.formatWithSign(transaction.amount, transaction.type),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = SoraFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = color
                )
                if (showTime) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = DateUtils.formatTime(transaction.dateMillis),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
