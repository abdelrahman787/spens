package com.masareefy.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.masareefy.app.data.TransactionDao
import com.masareefy.app.data.TransactionEntity
import com.masareefy.app.data.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainViewModel(
    private val transactionDao: TransactionDao,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val currentMonthPrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    
    val transactions = transactionDao.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monthlySpent = transactionDao.getMonthlySpent(currentMonthPrefix)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val monthlyBudget = userPreferencesRepository.monthlyBudget
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 3000.0)

    val userName = userPreferencesRepository.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "المستخدم")

    val isOnboardingDone = userPreferencesRepository.isOnboardingDone
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun updateMonthlyBudget(amount: Double) {
        viewModelScope.launch {
            userPreferencesRepository.setMonthlyBudget(amount)
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.setOnboardingDone()
        }
    }

    fun addTransaction(amount: Double, category: String, isIncome: Boolean, note: String?) {
        viewModelScope.launch {
            val dateStr = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            transactionDao.insertTransaction(
                TransactionEntity(
                    amount = amount,
                    category = category,
                    date = dateStr,
                    note = note,
                    isIncome = isIncome
                )
            )
        }
    }

    companion object {
        fun provideFactory(transactionDao: TransactionDao, userPreferencesRepository: UserPreferencesRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                    return MainViewModel(transactionDao, userPreferencesRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
