package com.example.data

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
}
