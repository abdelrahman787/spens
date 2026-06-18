package com.masareefy.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.Notifications
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
import com.masareefy.app.data.TransactionEntity
import com.masareefy.app.ui.MainViewModel
import java.util.Calendar

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
    val userName by viewModel.userName.collectAsState()
    
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "صباح الخير 👋"
        hour < 18 -> "مساء الخير 👋"
        else -> "مساء النور 👋"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(greeting, fontSize = 14.sp, color = Color.Gray)
                        Text(userName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "الإشعارات")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onVoiceInputClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.offset(y = (-32).dp)
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
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("صرفت النهارده", fontSize = 12.sp)
                        Text("${monthlySpent ?: 0.0} ج.م", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.error)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("متبقي للميزانية", fontSize = 12.sp)
                        Text("${monthlyBudget - (monthlySpent ?: 0.0)} ج.م", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("الميزانيات", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            // Placeholder category scroll
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(5) { index ->
                    Card(modifier = Modifier.width(120.dp).height(80.dp)) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("فئة $index")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("أحدث المعاملات", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = { /* TODO navigate to transactions list */ }) {
                    Text("مشاهدة الكل")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val recentTransactions = transactions.take(5)
            if (recentTransactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا توجد معاملات بعد", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(recentTransactions) { t ->
                        TransactionItem(t)
                    }
                }
            }
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
