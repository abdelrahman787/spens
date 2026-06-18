package com.masareefy.app.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.masareefy.app.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(viewModel: MainViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("الكل") }

    val filters = listOf("الكل") + transactions.map { it.category }.distinct().sorted()

    val filteredTransactions = transactions.filter { t ->
        val matchesSearch = searchQuery.isBlank() || 
            (t.note?.contains(searchQuery, ignoreCase = true) == true) ||
            t.category.contains(searchQuery, ignoreCase = true) ||
            t.amount.toString().contains(searchQuery)
        val matchesCategory = selectedCategory == "الكل" || t.category == selectedCategory
        matchesSearch && matchesCategory
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("المعاملات") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("ابحث في المعاملات (ملاحظة، فئة، مبلغ)...") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث") },
                trailingIcon = { 
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "مسح")
                        }
                    } else {
                        Icon(Icons.Default.Mic, contentDescription = "بحث صوتي")
                    }
                }
            )
            
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    FilterChip(
                        selected = filter == selectedCategory,
                        onClick = { selectedCategory = filter },
                        label = { Text(filter) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (filteredTransactions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("لا توجد معاملات مطابقة", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(horizontal = 16.dp)) {
                    items(filteredTransactions) { t ->
                        TransactionItem(t)
                    }
                }
            }
        }
    }
}

