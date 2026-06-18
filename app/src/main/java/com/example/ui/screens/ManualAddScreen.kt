package com.masareefy.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masareefy.app.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualAddScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("أخرى") }
    var isIncome by remember { mutableStateOf(false) }

    val categories = listOf(
        Pair("أكل ومطاعم", "🍔"), Pair("نقل ومواصلات", "🚗"), Pair("بقالة", "🛒"),
        Pair("إيجار وسكن", "🏠"), Pair("صحة", "💊"), Pair("أخرى", "📦")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isIncome) "إضافة دخل" else "إضافة مصروف") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    Switch(
                        checked = isIncome,
                        onCheckedChange = { isIncome = it },
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            
            // Amount Display
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("المبلغ", color = Color.Gray, fontSize = 14.sp)
                Text(
                    text = if (amountText.isEmpty()) "0" else amountText,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isIncome) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                )
                Text("ج.م", color = Color.Gray, fontSize = 16.sp)
            }

            // Categories Grid
            Column {
                categories.chunked(3).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { (catName, icon) ->
                            val selected = category == catName
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { category = catName }
                                    .padding(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(icon, fontSize = 24.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    catName,
                                    fontSize = 12.sp,
                                    color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
            
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* TODO Open Datepicker */ }) {
                    Icon(Icons.Default.DateRange, contentDescription = "التاريخ")
                }
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("ملاحظة (اختياري)") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        Icon(Icons.Default.CameraAlt, contentDescription = "إرفاق صورة")
                    }
                )
            }

            // Custom Keypad
            Column(modifier = Modifier.fillMaxWidth()) {
                val keys = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf(".", "0", "⌫")
                )
                keys.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { key ->
                            TextButton(
                                onClick = {
                                    when (key) {
                                        "⌫" -> if (amountText.isNotEmpty()) amountText = amountText.dropLast(1)
                                        "." -> if (!amountText.contains(".")) amountText += "."
                                        else -> if (amountText.length < 10) amountText += key
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                                    .height(60.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Text(key, fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount != null && amount > 0) {
                        viewModel.addTransaction(amount, category, isIncome, noteText)
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = amountText.toDoubleOrNull() ?: 0.0 > 0
            ) {
                Text("حفظ")
            }
        }
    }
}
