package com.samarth.aifinancecoach.presentation.splash
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.samarth.aifinancecoach.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No setContentView — we use a window background drawable
        // so the splash is shown instantly with zero layout inflation

        observeNavigation()
    }

    private fun observeNavigation() {
        lifecycleScope.launch {
            viewModel.navigationRoute.collect { route ->
                if (route != null) {
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    // Pass the start destination to MainActivity
                    intent.putExtra(MainActivity.START_DESTINATION, route)
                    startActivity(intent)
                    // Finish so user can't press back to splash
                    finish()
                }
            }
        }
    }
}
