package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TransactionEntity
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onVoiceInputClick: () -> Unit,
    onAddExpenseClick: () -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()
    val monthlySpent by viewModel.monthlySpent.collectAsState()
    val monthlyBudget by viewModel.monthlyBudget.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("مصاريفي", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onVoiceInputClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Mic, contentDescription = "تسجيل صوتي")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            BudgetCard(spent = monthlySpent ?: 0.0, budget = monthlyBudget)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("أحدث المعاملات", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = onAddExpenseClick) {
                    Text("إضافة يدوية")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (transactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا توجد معاملات بعد", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(transactions) { t ->
                        TransactionItem(t)
                    }
                }
            }
        }
    }
}

@Composable
fun BudgetCard(spent: Double, budget: Double) {
    val percentage = if (budget > 0) (spent / budget).toFloat() else 0f
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("الميزانية المتبقية", color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f), fontSize = 12.sp)
            Text("${budget - spent} ج.م", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("المنفق: $spent", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 12.sp)
                Text("الميزانية: $budget", color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { percentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
fun TransactionItem(transaction: TransactionEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(getCategoryIcon(transaction.category), fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.category, fontWeight = FontWeight.Bold)
                if (!transaction.note.isNullOrBlank()) {
                    Text(transaction.note, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Text(
                text = "${if (transaction.isIncome) "+" else "-"} ${transaction.amount} ج.م",
                color = if (transaction.isIncome) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun getCategoryIcon(category: String): String {
    return when {
        category.contains("أكل") || category.contains("طعام") -> "🍔"
        category.contains("مواصلات") || category.contains("نقل") -> "🚗"
        category.contains("بقالة") || category.contains("سوبرماركت") -> "🛒"
        category.contains("إيجار") || category.contains("سكن") -> "🏠"
        category.contains("صحة") || category.contains("دواء") -> "💊"
        category.contains("تسوق") || category.contains("ملابس") -> "🛍️"
        else -> "📦"
    }
}
