package com.masareefy.app

import android.content.Context
import com.masareefy.app.data.AppDatabase
import com.masareefy.app.data.TransactionDao
import com.masareefy.app.data.UserPreferencesRepository

class AppContainer(private val context: Context) {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(context) }
    val transactionDao: TransactionDao by lazy { database.transactionDao() }
    val budgetDao: com.masareefy.app.data.BudgetDao by lazy { database.budgetDao() }
    val expenseDao: com.masareefy.app.data.ExpenseDao by lazy { database.expenseDao() }
    val userPreferencesRepository: UserPreferencesRepository by lazy { UserPreferencesRepository(context) }
}
