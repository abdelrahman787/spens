package com.masareefy.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masareefy.app.ui.MainViewModel

@Composable
fun BudgetSettings(viewModel: MainViewModel) {
    val monthlyBudget by viewModel.monthlyBudget.collectAsState()
    val monthlySpent by viewModel.monthlySpent.collectAsState()
    
    // Instead of transactions, it could be either. The app uses monthlySpent from transactions.
    val remainingBalance = (monthlyBudget - (monthlySpent ?: 0.0)).coerceAtLeast(0.0)

    var isEditing by remember { mutableStateOf(false) }
    var newBudgetText by remember { mutableStateOf(monthlyBudget.toString()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("إعدادات الميزانية", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            if (isEditing) {
                OutlinedTextField(
                    value = newBudgetText,
                    onValueChange = { newBudgetText = it },
                    label = { Text("ميزانية الشهر (ج.م)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = {
                        isEditing = false
                        newBudgetText = monthlyBudget.toString()
                    }) {
                        Text("إلغاء")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val amount = newBudgetText.toDoubleOrNull()
                        if (amount != null && amount >= 0) {
                            viewModel.updateMonthlyBudget(amount)
                        }
                        isEditing = false
                    }) {
                        Text("حفظ")
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("الميزانية الحالية:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$monthlyBudget ج.م", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Button(onClick = {
                        isEditing = true
                        newBudgetText = monthlyBudget.toString()
                    }) {
                        Text("تعديل")
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("المتبقي من الميزانية:", fontWeight = FontWeight.Bold)
                Text(
                    "$remainingBalance ج.م",
                    fontWeight = FontWeight.Bold,
                    color = if (remainingBalance <= 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
