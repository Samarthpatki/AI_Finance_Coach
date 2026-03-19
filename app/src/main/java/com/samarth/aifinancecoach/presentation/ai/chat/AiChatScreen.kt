package com.samarth.aifinancecoach.presentation.ai.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.samarth.aifinancecoach.R
import com.samarth.aifinancecoach.domain.model.AiMessage
import com.samarth.aifinancecoach.domain.model.MessageRole
import com.samarth.aifinancecoach.presentation.theme.DmSansFontFamily
import com.samarth.aifinancecoach.presentation.theme.SoraFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    viewModel: AiChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding() // Ensures input bar moves up with keyboard
    ) {
        // Custom Top Bar (Attached to TabLayout above)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF00C896), Color(0xFF0096FF))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.ai_chat_title),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontFamily = SoraFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Powered by Gemini",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                if (state.messages.isNotEmpty()) {
                    IconButton(
                        onClick = viewModel::onClearChatClicked,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Clear Chat",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (state.messages.isEmpty()) {
                EmptyChatState(onPromptSelected = viewModel::onQuickPromptSelected)
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(state.messages) { message ->
                        MessageBubble(
                            message = message,
                            onCopy = {
                                clipboardManager.setText(AnnotatedString(message.content))
                            },
                            onRetry = viewModel::retryLastMessage
                        )
                    }
                }
            }
        }

        // Bottom Bar (Attached to bottom)
        ChatInputBar(
            inputText = state.inputText,
            onInputChanged = viewModel::onInputChanged,
            onSend = viewModel::onSendMessage,
            isLoading = state.isLoading
        )
    }

    if (state.showClearDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissClear,
            title = { Text(stringResource(R.string.ai_chat_clear_confirm)) },
            text = { Text(stringResource(R.string.ai_chat_clear_confirm_desc)) },
            confirmButton = {
                TextButton(onClick = viewModel::onConfirmClear) {
                    Text(stringResource(R.string.ai_chat_clear), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissClear) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MessageBubble(
    message: AiMessage,
    onCopy: () -> Unit,
    onRetry: () -> Unit
) {
    val isUser = message.role == MessageRole.USER
    val alignment = if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        if (isUser) {
            UserMessageBubble(message.content)
        } else {
            AssistantMessageBubble(
                content = message.content,
                isLoading = message.isLoading,
                isError = message.isError,
                onCopy = onCopy,
                onRetry = onRetry
            )
        }
    }
}

@Composable
fun UserMessageBubble(content: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .clip(RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF00C896), Color(0xFF0096FF))
                )
            )
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = DmSansFontFamily,
                color = Color.White
            )
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AssistantMessageBubble(
    content: String,
    isLoading: Boolean,
    isError: Boolean,
    onCopy: () -> Unit,
    onRetry: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
            .border(
                1.dp,
                if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
            )
            .combinedClickable(
                onLongClick = onCopy,
                onClick = { if (isError) onRetry() }
            ),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(10.dp, 8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "AI Coach",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            if (isLoading && content.isEmpty()) {
                LoadingDots()
            } else {
                FormattedText(
                    text = content,
                    isStreaming = isLoading
                )
            }
            if (isError) {
                Text(
                    text = "⚠ Error — Tap to retry",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun FormattedText(text: String, isStreaming: Boolean) {
    val annotatedString = parseMarkdown(text)
    
    Box {
        Text(
            text = annotatedString,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = DmSansFontFamily,
                lineHeight = 20.sp
            )
        )
        if (isStreaming) {
            Cursor()
        }
    }
}

@Composable
fun Cursor() {
    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )
    
    Box(
        modifier = Modifier
            .padding(start = 1.dp)
            .width(1.5.dp)
            .height(16.dp)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
    )
}

private fun parseMarkdown(text: String): AnnotatedString {
    return buildAnnotatedString {
        var remainingText = text
        while (remainingText.isNotEmpty()) {
            val boldMatch = Regex("\\*\\*(.*?)\\*\\*").find(remainingText)
            if (boldMatch != null) {
                append(remainingText.substring(0, boldMatch.range.first))
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(boldMatch.groupValues[1])
                }
                remainingText = remainingText.substring(boldMatch.range.last + 1)
            } else {
                append(remainingText)
                remainingText = ""
            }
        }
    }
}

@Composable
fun LoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 0),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        modifier = Modifier.padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Dot(dot1Alpha)
        Dot(dot2Alpha)
        Dot(dot3Alpha)
    }
}

@Composable
fun Dot(alpha: Float) {
    Box(
        modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = alpha))
    )
}

@Composable
fun ChatInputBar(
    inputText: String,
    onInputChanged: (String) -> Unit,
    onSend: () -> Unit,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp
    ) {
        Column {
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputChanged,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    placeholder = { 
                        Text(
                            stringResource(R.string.ai_chat_input_hint),
                            style = MaterialTheme.typography.bodyMedium
                        ) 
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    maxLines = 3,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(onSend = { onSend() })
                )

                val sendButtonColor by animateColorAsState(
                    targetValue = if (inputText.isNotBlank() && !isLoading) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    label = "sendButtonColor"
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (inputText.isNotBlank() && !isLoading) {
                                Brush.horizontalGradient(listOf(Color(0xFF00C896), Color(0xFF0096FF)))
                            } else {
                                Brush.linearGradient(listOf(sendButtonColor, sendButtonColor))
                            }
                        )
                        .border(
                            width = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = CircleShape
                        )
                        .clickable(enabled = inputText.isNotBlank() && !isLoading) { onSend() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (inputText.isNotBlank() && !isLoading) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyChatState(onPromptSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.ai_chat_empty_title),
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = SoraFontFamily,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.ai_chat_empty_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        
        val prompts = listOf(
            stringResource(R.string.ai_prompt_spending),
            stringResource(R.string.ai_prompt_savings),
            stringResource(R.string.ai_prompt_budget),
            stringResource(R.string.ai_prompt_advice),
            stringResource(R.string.ai_prompt_afford),
            stringResource(R.string.ai_prompt_compare)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(prompts) { prompt ->
                SuggestionChip(
                    onClick = { onPromptSelected(prompt) },
                    label = { 
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = DmSansFontFamily
                            ),
                            maxLines = 1
                        ) 
                    },
                    shape = RoundedCornerShape(14.dp),
                )
            }
        }
    }
}
