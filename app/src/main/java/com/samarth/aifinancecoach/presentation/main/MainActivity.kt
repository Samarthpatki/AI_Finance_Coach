package com.samarth.aifinancecoach.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.samarth.aifinancecoach.presentation.navigation.AppNavGraph
import com.samarth.aifinancecoach.presentation.theme.AIFinanceCoachTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    companion object {
        const val START_DESTINATION = "start_destination"
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        // Must be called BEFORE super.onCreate()
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Keep splash visible until auth state is determined
        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoading.value
        }

        enableEdgeToEdge()

        setContent {
            AIFinanceCoachTheme {
                AppNavGraph(
                    startDestination = viewModel.startDestination.value
                )

            }
        }
    }
}
