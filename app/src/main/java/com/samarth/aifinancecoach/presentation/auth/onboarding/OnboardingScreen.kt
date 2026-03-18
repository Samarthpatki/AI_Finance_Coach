package com.samarth.aifinancecoach.presentation.auth.onboarding

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.samarth.aifinancecoach.R
import com.samarth.aifinancecoach.presentation.navigation.Screen
import com.samarth.aifinancecoach.presentation.theme.DmSansFontFamily
import com.samarth.aifinancecoach.presentation.theme.SoraFontFamily
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    navController: NavController,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { state.totalPages })
    val backgroundColor = MaterialTheme.colorScheme.background


    LaunchedEffect(viewModel.navigationEvent) {
        viewModel.navigationEvent.collectLatest { route ->
            navController.navigate(route) {
                popUpTo(Screen.Onboarding.route) { inclusive = true }
            }
        }
    }

    LaunchedEffect(state.currentPage) {
        pagerState.animateScrollToPage(state.currentPage)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(bottom = 32.dp)
    ) {
        // Top Skip Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, end = 24.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Text(
                text = stringResource(R.string.onboarding_skip),
                style = MaterialTheme.typography.labelLarge.copy(fontFamily = DmSansFontFamily),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { viewModel.skipOnboarding() }
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            userScrollEnabled = false
        ) { pageIndex ->
            OnboardingPageContent(pageIndex)
        }

        // Bottom section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(state.totalPages) { index ->
                    val isActive = index == state.currentPage
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (isActive) 24.dp else 8.dp)
                            .clip(if (isActive) RoundedCornerShape(4.dp) else CircleShape)
                            .background(
                                if (isActive) {
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF00C896), Color(0xFF0096FF))
                                    )
                                } else {
                                    Brush.linearGradient(listOf(MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.outline))
                                }
                            )
                    )
                }
            }

            // Next / Get Started Button
            Button(
                onClick = { viewModel.nextPage() },
                modifier = Modifier
                    .height(56.dp)
                    .widthIn(min = 120.dp, max = 160.dp), // Reduced width
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
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
                    Text(
                        text = if (state.currentPage == state.totalPages - 1) 
                            stringResource(R.string.onboarding_get_started) 
                        else stringResource(R.string.onboarding_next),
                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = DmSansFontFamily),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(pageIndex: Int) {
    val title = when (pageIndex) {
        0 -> stringResource(R.string.onboarding_page1_title)
        1 -> stringResource(R.string.onboarding_page2_title)
        else -> stringResource(R.string.onboarding_page3_title)
    }
    val description = when (pageIndex) {
        0 -> stringResource(R.string.onboarding_page1_desc)
        1 -> stringResource(R.string.onboarding_page2_desc)
        else -> stringResource(R.string.onboarding_page3_desc)
    }

    val gradientOverlay = when (pageIndex) {
        0 -> Brush.verticalGradient(listOf(Color(0xFF00C896).copy(alpha = 0.15f), Color(0xFF0096FF).copy(alpha = 0.15f)))
        1 -> Brush.verticalGradient(listOf(Color(0xFF0096FF).copy(alpha = 0.15f), Color(0xFFFFB340).copy(alpha = 0.15f)))
        else -> Brush.verticalGradient(listOf(Color(0xFFFFB340).copy(alpha = 0.15f), Color(0xFF00C896).copy(alpha = 0.15f)))
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(gradientOverlay),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for illustration
            Surface(
                modifier = Modifier.size(120.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(24.dp)
            ) {
                 // Icon could go here
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.displayMedium.copy(fontFamily = SoraFontFamily),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 24.dp),
            textAlign = TextAlign.Start
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge.copy(fontFamily = DmSansFontFamily),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp),
            textAlign = TextAlign.Start
        )
    }
}
