package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.MainViewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ManualAddScreen
import com.example.ui.screens.VoiceInputScreen
import com.example.ui.theme.MyApplicationTheme

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
        factory = MainViewModel.provideFactory(appContainer.transactionDao)
    )

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onVoiceInputClick = { navController.navigate("voice_input") },
                onAddExpenseClick = { navController.navigate("manual_add") }
            )
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
                onManualEntry = { navController.navigate("manual_add") }
            )
        }
    }
}
