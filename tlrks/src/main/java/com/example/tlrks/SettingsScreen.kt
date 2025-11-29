package com.example.tlrks

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: QuestViewModel,
    onBack: () -> Unit
) {
    // ViewModel에 현재 저장된 설정 값
    var autoFail by remember { mutableStateOf(viewModel.settings.autoFailAfter24h) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 24시간 자동 실패 스위치
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Auto-fail quests after 24 hours")
                    Text(
                        text = "If enabled, unfinished quests older than 24h will be moved to Failed when you open the app.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Switch(
                    checked = autoFail,
                    onCheckedChange = { autoFail = it }
                )
            }

            Button(
                onClick = {
                    val newSettings = Settings(
                        autoFailAfter24h = autoFail
                    )
                    viewModel.updateSettings(newSettings)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}
