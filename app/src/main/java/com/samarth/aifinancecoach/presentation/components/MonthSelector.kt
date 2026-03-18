package com.samarth.aifinancecoach.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.samarth.aifinancecoach.presentation.theme.SoraFontFamily
import com.samarth.aifinancecoach.utils.DateUtils

@Composable
fun MonthSelector(
    month: Int,
    year: Int,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    canGoNext: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousClick) {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = "Previous Month",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = "${DateUtils.getMonthName(month)} $year",
            style = MaterialTheme.typography.titleSmall.copy(fontFamily = SoraFontFamily),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        IconButton(
            onClick = onNextClick,
            enabled = canGoNext
        ) {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = "Next Month",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.alpha(if (canGoNext) 1f else 0.4f)
            )
        }
    }
}
