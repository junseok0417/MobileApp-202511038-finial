package com.example.tlrks

import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// ────────────────────────────────────────
// 1) ViewModel을 감싸는 화면 (실제 앱에서 사용)
// ────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyQuestScreen(
    viewModel: QuestViewModel,
    onOpenSettings: () -> Unit,
    onOpenHistory: () -> Unit
) {
    val quests by remember { derivedStateOf { viewModel.quests } }

    DailyQuestScreenContent(
        quests = quests,
        onOpenSettings = onOpenSettings,
        onOpenHistory = onOpenHistory,
        onAddQuest = { title -> viewModel.addQuest(title) },
        onToggleTimer = { quest -> viewModel.toggleTimer(quest) },
        onComplete = { quest -> viewModel.completeQuest(quest) },
        onClearIncomplete = { viewModel.clearIncomplete() },
        onFailQuest = { quest -> viewModel.failQuest(quest) }
    )
}

// ────────────────────────────────────────
// 2) 순수 UI만 담당하는 내부 컴포저블
//    → Preview는 이걸 직접 호출
// ────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DailyQuestScreenContent(
    quests: List<Quest>,
    onOpenSettings: () -> Unit,
    onOpenHistory: () -> Unit,
    onAddQuest: (String) -> Unit,
    onToggleTimer: (Quest) -> Unit,
    onComplete: (Quest) -> Unit,
    onClearIncomplete: () -> Unit,
    onFailQuest: (Quest) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var questToDelete by remember { mutableStateOf<Quest?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Today's Quests",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    // 미완료 퀘스트 정리
                    if (quests.any { it.status == QuestStatus.ACTIVE }) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear Incomplete"
                            )
                        }
                    }
                    TextButton(onClick = onOpenHistory) {
                        Text("History")
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Quest"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (quests.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "You have no quests for today.",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap the + button to add your first quest.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(quests, key = { it.id }) { quest ->
                        QuestCard(
                            quest = quest,
                            onToggleTimer = { onToggleTimer(quest) },
                            onComplete = { onComplete(quest) },
                            onLongPressDelete = { questToDelete = quest }
                        )
                    }
                }
            }
        }
    }

    // 퀘스트 추가 다이얼로그
    if (showAddDialog) {
        AddQuestDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title ->
                onAddQuest(title)
                showAddDialog = false
            }
        )
    }

    // 미완료 전체 삭제 다이얼로그
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Incomplete Quests") },
            text = { Text("All active quests will be marked as failed. Continue?") },
            confirmButton = {
                TextButton(onClick = {
                    onClearIncomplete()
                    showClearDialog = false
                }) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 개별 삭제(길게 누르기)
    questToDelete?.let { q ->
        AlertDialog(
            onDismissRequest = { questToDelete = null },
            title = { Text("Delete Quest") },
            text = { Text("Mark this quest as failed and remove it?") },
            confirmButton = {
                TextButton(onClick = {
                    onFailQuest(q)
                    questToDelete = null
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { questToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ────────────────────────────────────────
// 카드 UI
// ────────────────────────────────────────
@Composable
fun QuestCard(
    quest: Quest,
    onToggleTimer: () -> Unit,
    onComplete: () -> Unit,
    onLongPressDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPressDelete() }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = if (quest.status == QuestStatus.ACTIVE)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 타이머 버튼
            IconButton(
                onClick = onToggleTimer,
                enabled = quest.status == QuestStatus.ACTIVE
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Toggle timer"
                )
            }

            // 제목 + 시간
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = quest.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (quest.status == QuestStatus.COMPLETED)
                        TextDecoration.LineThrough
                    else
                        TextDecoration.None,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatElapsedTime(quest.elapsedMillis),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // 완료 버튼
            IconButton(
                onClick = onComplete,
                enabled = quest.status == QuestStatus.ACTIVE
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Complete quest"
                )
            }
        }
    }
}

// ────────────────────────────────────────
// 추가 다이얼로그
// ────────────────────────────────────────
@Composable
fun AddQuestDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Quest") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Quest title") }
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onAdd(text) },
                enabled = text.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// ms → "HH:MM:SS"
fun formatElapsedTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

// ────────────────────────────────────────
// Preview들 (★ ViewModel 전혀 안 씀)
// ────────────────────────────────────────
@Preview(showBackground = true, backgroundColor = 0xFF111111)
@Composable
fun DailyQuestScreenPreview() {
    val sampleQuests = listOf(
        Quest(
            id = 1,
            title = "Study Kotlin for 30 minutes",
            elapsedMillis = 12_000,
            status = QuestStatus.ACTIVE,
            isRunning = true
        ),
        Quest(
            id = 2,
            title = "Read a book",
            elapsedMillis = 3_000,
            status = QuestStatus.COMPLETED,
            isRunning = false
        )
    )

    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        DailyQuestScreenContent(
            quests = sampleQuests,
            onOpenSettings = {},
            onOpenHistory = {},
            onAddQuest = {},
            onToggleTimer = {},
            onComplete = {},
            onClearIncomplete = {},
            onFailQuest = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF111111)
@Composable
fun QuestCardPreview() {
    val sampleQuest = Quest(
        id = 1,
        title = "Sample Quest",
        elapsedMillis = 12_340,
        status = QuestStatus.ACTIVE,
        isRunning = false
    )

    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        QuestCard(
            quest = sampleQuest,
            onToggleTimer = {},
            onComplete = {},
            onLongPressDelete = {}
        )
    }
}
