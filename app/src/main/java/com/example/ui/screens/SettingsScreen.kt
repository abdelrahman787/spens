package com.masareefy.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masareefy.app.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val userName by viewModel.userName.collectAsState()
    val monthlyBudget by viewModel.monthlyBudget.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("الإعدادات") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Profile Card
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(60.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(userName.firstOrNull()?.toString() ?: "م", color = MaterialTheme.colorScheme.onPrimary, fontSize = 24.sp)
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(userName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("الميزانية: $monthlyBudget ج.م", color = MaterialTheme.colorScheme.primary)
                }
            }

            Divider()

            ListItem(
                headlineContent = { Text("الميزانية الشهرية") },
                supportingContent = { Text("تعديل قيمة الميزانية") }
            )
            ListItem(
                headlineContent = { Text("المظهر") },
                supportingContent = { Text("فاتح / داكن") }
            )
            ListItem(
                headlineContent = { Text("مسح البيانات") },
                supportingContent = { Text("حذف جميع المعاملات") }
            )
        }
    }
}
