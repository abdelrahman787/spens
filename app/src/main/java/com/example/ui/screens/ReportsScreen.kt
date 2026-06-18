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
            
            val mockData = listOf(
                CategorySpending("أكل ومطاعم", 800.0),
                CategorySpending("نقل ومواصلات", 300.0),
                CategorySpending("بقالة", 400.0)
            )
            
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().height(150.dp)) {
                DonutChart(data = mockData, modifier = Modifier.size(100.dp))
            }
            // Legends
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                mockData.forEachIndexed { index, item ->
                    val color = listOf(Color(0xFFFF8F00), Color(0xFF0288D1), Color(0xFF7B1FA2))[index % 3]
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(modifier = Modifier.size(12.dp), shape = RoundedCornerShape(2.dp), color = color) {}
                            Spacer(Modifier.width(8.dp))
                            Text(item.category)
                        }
                        Text("${item.total} ج.م", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DonutChart(
    data: List<CategorySpending>,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color(0xFFFF8F00), Color(0xFF0288D1), Color(0xFF7B1FA2),
        Color(0xFF388E3C), Color(0xFFE53935), Color(0xFF00695C), Color(0xFF90A4AE)
    )
    Canvas(modifier = modifier) {
        val total = data.sumOf { it.total }
        var startAngle = -90f
        data.forEachIndexed { index, item ->
            val sweep = ((item.total / total) * 360f).toFloat()
            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = 40f, cap = StrokeCap.Butt)
            )
            startAngle += sweep
        }
    }
}
