package com.example

import android.content.Context
import com.example.data.AppDatabase
import com.example.data.TransactionDao

class AppContainer(private val context: Context) {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(context) }
    val transactionDao: TransactionDao by lazy { database.transactionDao() }
}
