package com.samarth.aifinancecoach.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        val ONBOARDING_COMPLETE  = booleanPreferencesKey("onboarding_complete")
        val PROFILE_COMPLETE     = booleanPreferencesKey("profile_complete")
        val USER_NAME            = stringPreferencesKey("user_name")
        val USER_CURRENCY        = stringPreferencesKey("user_currency")
        val DARK_MODE_ENABLED    = booleanPreferencesKey("dark_mode_enabled")
        val MONTHLY_INCOME       = stringPreferencesKey("monthly_income")
        val IS_USER_LOGGED_IN    = booleanPreferencesKey("is_user_logged_in")
    }

    // ── Onboarding ──────────────────────────────

    suspend fun setOnboardingComplete(complete: Boolean = true) {
        context.dataStore.edit { it[ONBOARDING_COMPLETE] = complete }
    }

    suspend fun isOnboardingComplete(): Boolean =
        context.dataStore.data.first()[ONBOARDING_COMPLETE] ?: false

    // ── Login ──────────────────────────────────

    suspend fun setUserLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit { it[IS_USER_LOGGED_IN] = loggedIn }
    }
    suspend fun isUserLoggedIn(): Boolean =
        context.dataStore.data.first()[IS_USER_LOGGED_IN] ?: false

    // ── Profile ──────────────────────────────────

    suspend fun setProfileSetupComplete(complete: Boolean = true) {
        context.dataStore.edit { it[PROFILE_COMPLETE] = complete }
    }

    suspend fun isProfileSetupComplete(): Boolean =
        context.dataStore.data.first()[PROFILE_COMPLETE] ?: false

    // ── User Prefs ───────────────────────────────

    suspend fun setUserName(name: String) {
        context.dataStore.edit { it[USER_NAME] = name }
    }

    fun getUserName(): Flow<String> =
        context.dataStore.data.map { it[USER_NAME] ?: "" }

    suspend fun setCurrency(currency: String) {
        context.dataStore.edit { it[USER_CURRENCY] = currency }
    }

    fun getCurrency(): Flow<String> =
        context.dataStore.data.map { it[USER_CURRENCY] ?: "INR" }

    suspend fun setMonthlyIncome(income: String) {
        context.dataStore.edit { it[MONTHLY_INCOME] = income }
    }

    fun getMonthlyIncome(): Flow<String> =
        context.dataStore.data.map { it[MONTHLY_INCOME] ?: "0" }

    // ── Dark Mode ────────────────────────────────

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE_ENABLED] = enabled }
    }

    fun isDarkModeEnabled(): Flow<Boolean> =
        context.dataStore.data.map { it[DARK_MODE_ENABLED] ?: true }
}
