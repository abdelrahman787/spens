package com.masareefy.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.masareefy.app.ui.MainViewModel
import com.masareefy.app.ui.screens.HomeScreen
import com.masareefy.app.ui.screens.ManualAddScreen
import com.masareefy.app.ui.screens.OnboardingScreen
import com.masareefy.app.ui.screens.ReportsScreen
import com.masareefy.app.ui.screens.SettingsScreen
import com.masareefy.app.ui.screens.TransactionsScreen
import com.masareefy.app.ui.screens.VoiceInputScreen
import com.masareefy.app.ui.theme.MyApplicationTheme
import com.masareefy.app.ui.theme.PrimaryTeal
import com.masareefy.app.ui.theme.InactiveGray

sealed class Screen(val route: String, val titleAr: String, val icon: ImageVector) {
    object Home : Screen("home", "الرئيسية", Icons.Outlined.Home)
    object Reports : Screen("reports", "التقارير", Icons.Outlined.BarChart)
    object Transactions : Screen("transactions", "المعاملات", Icons.Outlined.Receipt)
    object Settings : Screen("settings", "الإعدادات", Icons.Outlined.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appContainer = (application as MasareefyApplication).container

        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MasareefyApp(appContainer)
                }
            }
        }
    }
}

@Composable
fun MasareefyApp(appContainer: AppContainer) {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModel.provideFactory(appContainer.transactionDao, appContainer.userPreferencesRepository)
    )

    val isOnboardingDone by viewModel.isOnboardingDone.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        Screen.Home,
        Screen.Reports,
        Screen.Transactions,
        Screen.Settings
    )

    val showBottomNav = currentDestination?.route in items.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                NavigationBar(
                    containerColor = Color.White
                ) {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.titleAr) },
                            label = { Text(screen.titleAr) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryTeal,
                                selectedTextColor = PrimaryTeal,
                                unselectedIconColor = InactiveGray,
                                unselectedTextColor = InactiveGray,
                                indicatorColor = com.masareefy.app.ui.theme.PrimaryTealSurface
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isOnboardingDone) "home" else "onboarding",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("onboarding") {
                OnboardingScreen(viewModel = viewModel, onComplete = {
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                })
            }
            composable("home") {
                HomeScreen(
                    viewModel = viewModel,
                    onVoiceInputClick = { navController.navigate("voice_input") },
                    onAddExpenseClick = { navController.navigate("manual_add") }
                )
            }
            composable("reports") {
                ReportsScreen(viewModel = viewModel)
            }
            composable("transactions") {
                TransactionsScreen(viewModel = viewModel)
            }
            composable("settings") {
                SettingsScreen(viewModel = viewModel)
            }
            composable("budget") {
                com.masareefy.app.ui.screens.BudgetScreen(viewModel = viewModel)
            }
            composable("manual_add") {
                ManualAddScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("voice_input") {
                VoiceInputScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onManualEntry = {
                        navController.popBackStack()
                        navController.navigate("manual_add")
                    }
                )
            }
        }
    }
}
