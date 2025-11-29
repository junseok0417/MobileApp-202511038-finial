package com.example.tlrksrma  // ← 네 패키지 이름으로 변경

enum class QuestStatus {
    ACTIVE,
    COMPLETED,
    FAILED
}

data class Quest(
    val id: Long = System.currentTimeMillis(),
    var title: String,
    var elapsedMillis: Long = 0L,
    var isRunning: Boolean = false,
    var status: QuestStatus = QuestStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis(),
    var completedAt: Long? = null,
    var failedAt: Long? = null
)
