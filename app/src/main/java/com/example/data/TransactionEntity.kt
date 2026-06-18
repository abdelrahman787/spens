package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val category: String,
    val date: String, // Stored as ISO string YYYY-MM-DD
    val note: String?,
    val isIncome: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)
