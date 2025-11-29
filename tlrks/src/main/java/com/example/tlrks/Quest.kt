package com.example.tlrks


enum class QuestStatus {
    ACTIVE,
    COMPLETED,
    FAILED
}

data class Quest(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    var elapsedMillis: Long = 0L,
    var isRunning: Boolean = false,
    var status: QuestStatus = QuestStatus.ACTIVE,
    var completedAt: Long? = null,
    var failedAt: Long? = null
)
