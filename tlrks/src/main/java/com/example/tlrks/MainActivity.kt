package com.example.tlrks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*

enum class MainScreen {
    TODAY,
    SETTINGS,
    HISTORY
}

class MainActivity : ComponentActivity() {

    private val questViewModel: QuestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var currentScreen by remember { mutableStateOf(MainScreen.TODAY) }

            MaterialTheme(
                colorScheme = darkColorScheme()
            ) {
                Surface {
                    when (currentScreen) {
                        MainScreen.TODAY -> DailyQuestScreen(
                            viewModel = questViewModel,
                            onOpenSettings = { currentScreen = MainScreen.SETTINGS },
                            onOpenHistory = { currentScreen = MainScreen.HISTORY }
                        )

                        MainScreen.SETTINGS -> SettingsScreen(
                            viewModel = questViewModel,
                            onBack = { currentScreen = MainScreen.TODAY }
                        )

                        MainScreen.HISTORY -> HistoryScreen(
                            history = questViewModel.history,
                            onBack = { currentScreen = MainScreen.TODAY }
                        )
                    }
                }
            }
        }
    }
}
