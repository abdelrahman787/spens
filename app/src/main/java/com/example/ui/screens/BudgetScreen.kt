package com.masareefy.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masareefy.app.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(viewModel: MainViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الميزانية") }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* TODO */ },
                icon = { Icon(Icons.Default.Add, "إضافة فئة") },
                text = { Text("ميزانية جديدة") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Placeholder for Budget Donut
            Card(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Canvas(modifier = Modifier.size(120.dp)) {
                        drawArc(
                            color = Color(0xFF00695C),
                            startAngle = -90f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = 40f, cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = Color.LightGray,
                            startAngle = 180f,
                            sweepAngle = 90f,
                            useCenter = false,
                            style = Stroke(width = 40f, cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("الإجمالي", fontSize = 12.sp, color = Color.Gray)
                        Text("3000 ج.م", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            
            Text("الميزانيات المقسمة", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))

            // Placeholder Category Budget
            BudgetCategoryItem("أكل ومطاعم", "🍔", 1500.0, 2000.0)
            BudgetCategoryItem("نقل ومواصلات", "🚗", 400.0, 500.0)
            BudgetCategoryItem("بقالة", "🛒", 1200.0, 1000.0) // over budget
        }
    }
}

@Composable
fun BudgetCategoryItem(name: String, icon: String, spent: Double, total: Double) {
    val progress = (spent / total).toFloat()
    val color = when {
        progress >= 1f -> Color(0xFFC62828)
        progress >= 0.7f -> Color(0xFFF57C00)
        else -> Color(0xFF2E7D32)
    }

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(icon, fontSize = 24.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(name, fontWeight = FontWeight.Bold)
                }
                if (progress >= 1f) {
                    Badge(containerColor = MaterialTheme.colorScheme.error) { Text("تجاوز!") }
                } else if (progress >= 0.8f) {
                    Badge(containerColor = Color(0xFFF57C00)) { Text("تحذير") }
                }
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress.coerceAtMost(1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = color,
                trackColor = Color.LightGray
            )
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("$spent / $total ج.م", fontSize = 12.sp, color = Color.Gray)
                Text(
                    if (progress >= 1f) "متجاوز بـ ${spent - total}" else "متبقي ${total - spent}",
                    fontSize = 12.sp, color = color, fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
