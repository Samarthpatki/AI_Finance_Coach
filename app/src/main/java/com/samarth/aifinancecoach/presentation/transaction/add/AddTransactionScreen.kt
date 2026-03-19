package com.samarth.aifinancecoach.presentation.transaction.add

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.samarth.aifinancecoach.R
import com.samarth.aifinancecoach.domain.model.AccountType
import com.samarth.aifinancecoach.domain.model.Category
import com.samarth.aifinancecoach.domain.model.TransactionType
import com.samarth.aifinancecoach.presentation.theme.DmSansFontFamily
import com.samarth.aifinancecoach.presentation.theme.ErrorRed
import com.samarth.aifinancecoach.presentation.theme.SoraFontFamily
import com.samarth.aifinancecoach.presentation.theme.SuccessGreen
import com.samarth.aifinancecoach.utils.DateUtils
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navController: NavController,
    transactionId: Long? = null,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { event ->
            if (event == "popBackStack") {
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.isEditMode) stringResource(R.string.edit_transaction_title)
                        else stringResource(R.string.add_transaction_title),
                        style = MaterialTheme.typography.headlineMedium.copy(fontFamily = SoraFontFamily)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Button(
                onClick = { viewModel.onSaveClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                enabled = !state.isLoading
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00C896), Color(0xFF0096FF))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = if (state.isEditMode) stringResource(R.string.add_transaction_update)
                            else stringResource(R.string.add_transaction_save),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = DmSansFontFamily,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Transaction Type Selector
            TypeSelector(
                selectedType = state.selectedType,
                onTypeSelected = { viewModel.onTypeSelected(it) }
            )

            // Amount Input
            AmountInput(
                amount = state.amount,
                onAmountChanged = { viewModel.onAmountChanged(it) },
                type = state.selectedType,
                error = state.amountError
            )

            // Category Selection
            CategoryGrid(
                selectedCategory = state.selectedCategory,
                onCategorySelected = { viewModel.onCategorySelected(it) },
                type = state.selectedType,
                error = state.categoryError
            )

            // Account Selection
            AccountSelector(
                selectedAccount = state.selectedAccount,
                onAccountSelected = { viewModel.onAccountSelected(it) }
            )

            // Date Picker
            DatePickerSection(
                selectedDateMillis = state.selectedDateMillis,
                onDateSelected = { viewModel.onDateSelected(it) }
            )

            // Note Input
            OutlinedTextField(
                value = state.note,
                onValueChange = { viewModel.onNoteChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.add_transaction_note_label)) },
                placeholder = { Text(stringResource(R.string.add_transaction_note_hint)) },
                shape = RoundedCornerShape(8.dp),
                maxLines = 3
            )

            // Recurring Section
            RecurringSection(
                isRecurring = state.isRecurring,
                onRecurringToggled = { viewModel.onRecurringToggled(it) },
                intervalDays = state.recurringIntervalDays,
                onIntervalChanged = { viewModel.onRecurringIntervalChanged(it) }
            )
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun TypeSelector(
    selectedType: TransactionType,
    onTypeSelected: (TransactionType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TransactionType.entries.forEach { type ->
            val isSelected = selectedType == type
            val backgroundColor by animateColorAsState(
                if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                label = "backgroundColor"
            )
            val contentColor by animateColorAsState(
                if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                label = "contentColor"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(backgroundColor)
                    .clickable { onTypeSelected(type) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (type == TransactionType.EXPENSE) stringResource(R.string.add_transaction_expense)
                    else stringResource(R.string.add_transaction_income),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = DmSansFontFamily,
                        color = contentColor
                    )
                )
            }
        }
    }
}

@Composable
private fun AmountInput(
    amount: String,
    onAmountChanged: (String) -> Unit,
    type: TransactionType,
    error: String?
) {
    val color by animateColorAsState(
        if (type == TransactionType.EXPENSE) ErrorRed else SuccessGreen,
        label = "amountColor"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "₹",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = SoraFontFamily,
                    color = color
                )
            )
            TextField(
                value = amount,
                onValueChange = onAmountChanged,
                placeholder = {
                    Text(
                        text = "0.00",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                },
                textStyle = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = SoraFontFamily,
                    color = color,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = color,
                    unfocusedIndicatorColor = color.copy(alpha = 0.5f)
                ),
                modifier = Modifier.widthIn(min = 100.dp, max = 250.dp)
            )
        }
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun CategoryGrid(
    selectedCategory: Category?,
    onCategorySelected: (Category) -> Unit,
    type: TransactionType,
    error: String?
) {
    val categories = if (type == TransactionType.EXPENSE) {
        listOf(
            Category.FOOD, Category.TRANSPORT, Category.SHOPPING, Category.BILLS,
            Category.EMI, Category.HEALTH, Category.ENTERTAINMENT, Category.GROCERIES,
            Category.EDUCATION, Category.TRAVEL, Category.OTHER
        )
    } else {
        listOf(Category.SALARY, Category.FREELANCE, Category.INVESTMENT, Category.OTHER)
    }

    Column {
        Text(
            text = stringResource(R.string.add_transaction_category_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.height(if (type == TransactionType.EXPENSE) 250.dp else 100.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            userScrollEnabled = false
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                val scale by animateFloatAsState(if (isSelected) 1.1f else 1f, label = "categoryScale")

                Column(
                    modifier = Modifier
                        .scale(scale)
                        .clickable { onCategorySelected(category) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(category.color.copy(alpha = 0.15f))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = category.emoji, fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = category.name.lowercase(Locale.ROOT).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun AccountSelector(
    selectedAccount: AccountType,
    onAccountSelected: (AccountType) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.add_transaction_account_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AccountType.entries.forEach { account ->
                val isSelected = selectedAccount == account
                FilterChip(
                    selected = isSelected,
                    onClick = { onAccountSelected(account) },
                    label = { Text(account.name) },
                    leadingIcon = {
                        Icon(
                            imageVector = when (account) {
                                AccountType.CASH -> Icons.Rounded.Payments
                                AccountType.UPI -> Icons.Rounded.Smartphone
                                AccountType.BANK -> Icons.Rounded.AccountBalance
                            },
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerSection(
    selectedDateMillis: Long,
    onDateSelected: (Long) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column {
        Text(
            text = stringResource(R.string.add_transaction_date_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = DateUtils.formatFullDateTime(selectedDateMillis),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun RecurringSection(
    isRecurring: Boolean,
    onRecurringToggled: (Boolean) -> Unit,
    intervalDays: Int,
    onIntervalChanged: (Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(R.string.add_transaction_recurring_label),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Auto-log this every month",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = isRecurring,
                onCheckedChange = onRecurringToggled,
                colors = SwitchDefaults.colors(checkedTrackColor = MaterialTheme.colorScheme.primary)
            )
        }

        androidx.compose.animation.AnimatedVisibility(visible = isRecurring) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    text = stringResource(R.string.add_transaction_recurring_interval),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Daily" to 1, "Weekly" to 7, "Monthly" to 30).forEach { (label, days) ->
                        val isSelected = intervalDays == days
                        FilterChip(
                            selected = isSelected,
                            onClick = { onIntervalChanged(days) },
                            label = { Text(label) },
                            shape = RoundedCornerShape(20.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}
