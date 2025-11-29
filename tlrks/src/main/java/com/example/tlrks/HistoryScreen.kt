package com.example.tlrks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.darkColorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    history: List<Quest>,
    onBack: () -> Unit
) {
    val completed = history.filter { it.status == QuestStatus.COMPLETED }
    val failed = history.filter { it.status == QuestStatus.FAILED }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quest History") },
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
                .padding(16.dp)
        ) {
            // 완료된 퀘스트 섹션
            if (completed.isNotEmpty()) {
                Text(
                    text = "Completed Quests",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                val completedByDate = groupByDate(completed, useCompletedAt = true)

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    completedByDate.forEach { (date, questsOfDay) ->
                        item {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        items(questsOfDay) { q ->
                            HistoryRowCompleted(q)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // 실패한 퀘스트 섹션
            if (failed.isNotEmpty()) {
                Text(
                    text = "Failed Quests",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                val failedByDate = groupByDate(failed, useCompletedAt = false)

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    failedByDate.forEach { (date, questsOfDay) ->
                        item {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        items(questsOfDay) { q ->
                            HistoryRowFailed(q)
                        }
                    }
                }
            }

            if (completed.isEmpty() && failed.isEmpty()) {
                Text("No history yet.")
            }
        }
    }
}

private fun groupByDate(list: List<Quest>, useCompletedAt: Boolean): Map<String, List<Quest>> {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return list.groupBy { q ->
        val time = if (useCompletedAt) q.completedAt ?: q.createdAt else q.failedAt ?: q.createdAt
        sdf.format(Date(time))
    }.toSortedMap(compareByDescending { it }) // 최근 날짜가 위로 오도록
}

@Composable
private fun HistoryRowCompleted(quest: Quest) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = quest.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = formatElapsedTime(quest.elapsedMillis),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun HistoryRowFailed(quest: Quest) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = quest.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "Failed",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF111111)
@Composable
fun HistoryScreenPreview() {
    // 예시용 더미 데이터
    val sampleHistory = listOf(
        Quest(
            id = 1,
            title = "Study Kotlin for 30 minutes",
            elapsedMillis = 30 * 60 * 1000L,
            status = QuestStatus.COMPLETED,
            isRunning = false,
            createdAt = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000L,
            completedAt = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000L + 30 * 60 * 1000L
        ),
        Quest(
            id = 2,
            title = "Workout",
            elapsedMillis = 15 * 60 * 1000L,
            status = QuestStatus.FAILED,
            isRunning = false,
            createdAt = System.currentTimeMillis() - 24 * 60 * 60 * 1000L,
            failedAt = System.currentTimeMillis() - 23 * 60 * 60 * 1000L
        )
    )

    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        HistoryScreen(
            history = sampleHistory,
            onBack = {}   // 미리보기라서 비워둠
        )
    }
}
