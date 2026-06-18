package com.masareefy.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "masareefy_settings")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        val MONTHLY_BUDGET = doublePreferencesKey("monthly_budget")
        val USER_NAME = stringPreferencesKey("user_name")
        val IS_ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val CURRENCY = stringPreferencesKey("currency")
        val BUDGET_START_DAY = stringPreferencesKey("budget_start_day")
    }

    val monthlyBudget: Flow<Double> = context.dataStore.data.map { prefs ->
        prefs[MONTHLY_BUDGET] ?: 3000.0
    }

    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME] ?: "المستخدم"
    }

    val isOnboardingDone: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_ONBOARDING_DONE] ?: false
    }

    suspend fun setMonthlyBudget(budget: Double) {
        context.dataStore.edit { prefs -> prefs[MONTHLY_BUDGET] = budget }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { prefs -> prefs[USER_NAME] = name }
    }

    suspend fun setOnboardingDone() {
        context.dataStore.edit { prefs -> prefs[IS_ONBOARDING_DONE] = true }
    }
}
