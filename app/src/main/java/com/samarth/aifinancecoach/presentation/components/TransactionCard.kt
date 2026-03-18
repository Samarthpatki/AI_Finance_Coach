package com.samarth.aifinancecoach.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.samarth.aifinancecoach.domain.model.Transaction
import com.samarth.aifinancecoach.domain.model.TransactionType
import com.samarth.aifinancecoach.presentation.theme.SoraFontFamily
import com.samarth.aifinancecoach.utils.CurrencyFormatter
import com.samarth.aifinancecoach.utils.DateUtils

@Composable
fun TransactionCard(
    transaction: Transaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Emoji Circle
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = transaction.category.emoji, style = MaterialTheme.typography.titleLarge)
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.category.label,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = if (transaction.note.isNotEmpty()) transaction.note else transaction.account.name,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        // Amount & Date
        Column(horizontalAlignment = Alignment.End) {
            val amountText = if (transaction.type == TransactionType.INCOME) {
                "+${CurrencyFormatter.format(transaction.amount)}"
            } else {
                "-${CurrencyFormatter.format(transaction.amount)}"
            }
            val amountColor = if (transaction.type == TransactionType.INCOME) {
                Color(0xFF27C93F) // SuccessGreen
            } else {
                Color(0xFFFF4D6A) // ErrorRed
            }

            Text(
                text = amountText,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = SoraFontFamily,
                    color = amountColor,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = DateUtils.formatDate(transaction.dateMillis),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
