package com.samarth.aifinancecoach.presentation.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.samarth.aifinancecoach.BuildConfig
import com.samarth.aifinancecoach.R
import com.samarth.aifinancecoach.presentation.theme.DmSansFontFamily
import com.samarth.aifinancecoach.presentation.theme.SoraFontFamily
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { route ->
            navController.navigate(route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = SoraFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileHeaderSection(state, viewModel::onProfilePhotoSelected)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            ProfileFieldsCard(state, viewModel)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            PreferencesSection(state, viewModel::onDarkModeToggled)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AccountSection(viewModel::onSignOutClicked)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            AppInfoSection()
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (state.showSignOutDialog) {
        SignOutDialog(
            onConfirm = viewModel::onConfirmSignOut,
            onDismiss = viewModel::onDismissSignOut
        )
    }
}

@Composable
fun ProfileHeaderSection(
    state: SettingsState,
    onPhotoSelected: (Uri?) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> onPhotoSelected(uri) }

    Column(
        modifier = Modifier.padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(
                        2.dp,
                        Brush.horizontalGradient(listOf(Color(0xFF00C896), Color(0xFF0096FF))),
                        CircleShape
                    )
            ) {
                if (state.photoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = state.photoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
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
                        Text(
                            text = state.name.firstOrNull()?.toString() ?: "",
                            style = MaterialTheme.typography.displaySmall.copy(
                                fontFamily = SoraFontFamily,
                                color = Color.White
                            )
                        )
                    }
                }
            }
            if (state.isUploadingPhoto) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = Color.White
                )
            }
        }
        
        TextButton(onClick = { launcher.launch("image/*") }) {
            Text(
                text = stringResource(R.string.settings_dp_change),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontFamily = DmSansFontFamily,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
fun ProfileFieldsCard(
    state: SettingsState,
    viewModel: SettingsViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileTextField(
                label = stringResource(R.string.settings_name_label),
                value = state.name,
                onValueChange = viewModel::onNameChanged,
                placeholder = stringResource(R.string.settings_name_hint),
                leadingIcon = Icons.Rounded.Person,
                error = state.nameError
            )

            ProfileTextField(
                label = stringResource(R.string.settings_email_label),
                value = state.email,
                onValueChange = {},
                enabled = false,
                leadingIcon = Icons.Rounded.Email,
                trailingIcon = Icons.Rounded.Lock
            )

            ProfileTextField(
                label = stringResource(R.string.settings_income_label),
                value = state.monthlyIncome,
                onValueChange = viewModel::onIncomeChanged,
                placeholder = stringResource(R.string.settings_income_hint),
                leadingIconText = "₹",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                error = state.incomeError
            )

            CurrencySection(state.selectedCurrency, viewModel::onCurrencySelected)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = viewModel::onSaveClicked,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                enabled = !state.isSaving && state.name.isNotBlank() && state.monthlyIncome.isNotBlank()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFF00C896), Color(0xFF0096FF))),
                            alpha = if (state.name.isNotBlank() && state.monthlyIncome.isNotBlank()) 1f else 0.5f
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text(
                            text = stringResource(R.string.settings_save_profile),
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontFamily = DmSansFontFamily
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    enabled: Boolean = true,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    leadingIconText: String? = null,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    error: String? = null
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            placeholder = { Text(placeholder) },
            leadingIcon = {
                if (leadingIcon != null) {
                    Icon(leadingIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                } else if (leadingIconText != null) {
                    Text(
                        text = leadingIconText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            },
            trailingIcon = {
                if (trailingIcon != null) {
                    Icon(
                        trailingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            },
            shape = RoundedCornerShape(8.dp),
            isError = error != null,
            keyboardOptions = keyboardOptions,
            colors = OutlinedTextFieldDefaults.colors(
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                disabledBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
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
fun CurrencySection(
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit
) {
    val currencies = listOf(
        "INR" to "₹", "USD" to "$", "EUR" to "€",
        "GBP" to "£", "AED" to "د.إ", "SGD" to "S$"
    )

    Column {
        Text(
            text = stringResource(R.string.settings_currency_label),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(currencies) { (code, symbol) ->
                val isSelected = selectedCurrency == code
                Surface(
                    onClick = { onCurrencySelected(code) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.surface,
                    border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.height(36.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(
                                if (isSelected) Brush.horizontalGradient(listOf(Color(0xFF00C896), Color(0xFF0096FF)))
                                else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                            )
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$symbol $code",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PreferencesSection(
    state: SettingsState,
    onDarkModeToggled: () -> Unit
) {
    Column {
        Text(
            text = "Preferences",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (state.isDarkMode) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.settings_dark_mode),
                        style = MaterialTheme.typography.titleSmall.copy(fontFamily = SoraFontFamily)
                    )
                    Text(
                        text = stringResource(R.string.settings_dark_mode_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = state.isDarkMode,
                    onCheckedChange = { onDarkModeToggled() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Composable
fun AccountSection(onSignOut: () -> Unit) {
    Column {
        Text(
            text = "Account",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { onSignOut() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Logout,
                    contentDescription = null,
                    tint = Color(0xFFFF4D6A),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.settings_sign_out),
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = SoraFontFamily,
                        color = Color(0xFFFF4D6A)
                    ),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AppInfoSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = "AI Finance Coach",
            style = MaterialTheme.typography.titleSmall.copy(fontFamily = SoraFontFamily)
        )
        Text(
            text = "Version ${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.settings_made_with),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SignOutDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null, tint = Color(0xFFFF4D6A)) },
        title = {
            Text(
                text = stringResource(R.string.settings_sign_out_confirm),
                style = MaterialTheme.typography.titleMedium.copy(fontFamily = SoraFontFamily)
            )
        },
        text = {
            Text(
                text = stringResource(R.string.settings_sign_out_confirm_desc),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.settings_sign_out_yes), color = Color(0xFFFF4D6A))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.settings_sign_out_cancel), color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}
