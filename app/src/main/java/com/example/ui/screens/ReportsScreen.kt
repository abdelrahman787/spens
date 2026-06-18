package com.masareefy.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masareefy.app.data.CategorySpending
import com.masareefy.app.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(viewModel: MainViewModel) {
    val periods = listOf("اليوم", "الأسبوع", "الشهر", "٣ أشهر", "السنة")
    var selectedPeriod by remember { mutableStateOf("الشهر") }
    
    val expenses by viewModel.expenses.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("التقارير") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                periods.forEach { period ->
                    FilterChip(
                        selected = period == selectedPeriod,
                        onClick = { selectedPeriod = period },
                        label = { Text(period) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Summary Card
            Card(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("المنفق", fontSize = 12.sp)
                        Text("1500", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("الميزانية", fontSize = 12.sp)
                        Text("3000", fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("المتبقي", fontSize = 12.sp)
                        Text("1500", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                SuggestionChip(
                    onClick = {},
                    label = { Text("أقل من الشهر الماضي بـ ٣٢٠ ج.م") },
                    colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFE8F5E9))
                )
            }

            Spacer(Modifier.height(16.dp))
            Text("الإنفاق حسب الفئة", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold)
            
            // Recharts-inspired Monthly Expense Chart
            val categoryExpenses = expenses.groupBy { it.category }.mapValues { it.value.sumOf { exp -> exp.amount } }.toList()
            val chartData = if (categoryExpenses.isEmpty()) {
                listOf(Pair("لا يوجد بيانات", 1.0))
            } else {
                categoryExpenses
            }
            
            Box(modifier = Modifier.fillMaxWidth().height(200.dp).padding(16.dp)) {
                MonthlyExpenseChart(data = chartData)
            }
            
            // Legends
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                chartData.forEachIndexed { index, item ->
                    val color = listOf(Color(0xFFFF8F00), Color(0xFF0288D1), Color(0xFF7B1FA2), Color(0xFF388E3C), Color(0xFFE53935))[index % 5]
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(modifier = Modifier.size(12.dp), shape = RoundedCornerShape(2.dp), color = color) {}
                            Spacer(Modifier.width(8.dp))
                            Text(item.first)
                        }
                        Text("${item.second} ج.م", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MonthlyExpenseChart(data: List<Pair<String, Double>>) {
    val colors = listOf(Color(0xFFFF8F00), Color(0xFF0288D1), Color(0xFF7B1FA2), Color(0xFF388E3C), Color(0xFFE53935))
    Canvas(modifier = Modifier.fillMaxSize()) {
        val maxAmount = data.maxOfOrNull { it.second }?.toFloat() ?: 1f
        val maxLabelHeight = 40f
        val canvasWidth = size.width
        val canvasHeight = size.height - maxLabelHeight
        
        val barWidth = canvasWidth / (data.size * 2)
        val spacing = barWidth
        
        data.forEachIndexed { index, item ->
            val barHeight = ((item.second.toFloat() / maxAmount) * canvasHeight).coerceAtLeast(10f)
            val left = index * (barWidth + spacing) + spacing / 2
            val top = canvasHeight - barHeight
            
            drawRoundRect(
                color = colors[index % colors.size],
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(8f, 8f)
            )
        }
    }
}
