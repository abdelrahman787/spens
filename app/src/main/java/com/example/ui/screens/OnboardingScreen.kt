package com.masareefy.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masareefy.app.R
import com.masareefy.app.ui.MainViewModel

@Composable
fun OnboardingScreen(viewModel: MainViewModel, onComplete: () -> Unit) {
    var step by remember { mutableStateOf(1) }
    var budgetText by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (step) {
            1 -> {
                Image(
                    painter = painterResource(id = R.drawable.wallet_icon_1781766764407),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(120.dp)
                )
                Spacer(Modifier.height(32.dp))
                Text("أهلاً بيك في مصاريفي", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(16.dp))
                Text("تتبع مصاريفك وميزانيتك بصوتك", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(Modifier.height(48.dp))
                Button(onClick = { step = 2 }, modifier = Modifier.fillMaxWidth().height(50.dp)) {
                    Text("ابدأ")
                }
            }
            2 -> {
                Text("حدد ميزانيتك الشهرية", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(32.dp))
                Text(
                    text = if (budgetText.isEmpty()) "0" else budgetText,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("ج.م", fontSize = 16.sp)
                
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("2000", "3000", "5000", "10000").forEach { amount ->
                        SuggestionChip(onClick = { budgetText = amount }, label = { Text(amount) })
                    }
                }
                Spacer(Modifier.height(32.dp))
                
                Button(
                    onClick = {
                        val budget = budgetText.toDoubleOrNull() ?: 3000.0
                        viewModel.updateMonthlyBudget(budget)
                        step = 3
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("التالي")
                }
            }
            3 -> {
                Text("سجّل مصاريفك بصوتك!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Text("\"صرفت ١٥٠ جنيه على المواصلات\"", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                Spacer(Modifier.height(48.dp))
                
                Button(
                    onClick = {
                        viewModel.completeOnboarding()
                        onComplete()
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("ابدأ الاستخدام")
                }
            }
        }
    }
}
