package com.masareefy.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC, createdAt DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE date LIKE :monthPrefix || '%' AND isIncome = 0")
    fun getMonthlySpent(monthPrefix: String): Flow<Double?>

    @Insert
    suspend fun insertTransaction(transaction: TransactionEntity)

    @androidx.room.Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @androidx.room.Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE date LIKE :monthPrefix || '%' ORDER BY date DESC, createdAt DESC")
    fun getTransactionsByMonth(monthPrefix: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE category = :category AND date LIKE :monthPrefix || '%'")
    fun getTransactionsByCategory(category: String, monthPrefix: String): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE date LIKE :monthPrefix || '%' AND isIncome = 1")
    fun getMonthlyIncome(monthPrefix: String): Flow<Double?>

    @Query("SELECT category, SUM(amount) as total FROM transactions WHERE date LIKE :monthPrefix || '%' AND isIncome = 0 GROUP BY category ORDER BY total DESC")
    fun getSpendingByCategory(monthPrefix: String): Flow<List<CategorySpending>>

    @Query("SELECT date, SUM(amount) as total FROM transactions WHERE isIncome = 0 AND date >= :startDate GROUP BY date ORDER BY date DESC")
    fun getDailySpending(startDate: String): Flow<List<DailySpending>>
}

data class CategorySpending(val category: String, val total: Double)
data class DailySpending(val date: String, val total: Double)
