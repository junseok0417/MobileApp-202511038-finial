package com.example.tlrks

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuestViewModel(app: Application) : AndroidViewModel(app) {

    // 오늘의 퀘스트 리스트
    private val _quests = mutableStateListOf<Quest>()
    val quests: List<Quest> get() = _quests

    // 완료/실패 기록용 리스트
    private val _history = mutableStateListOf<Quest>()
    val history: List<Quest> get() = _history

    // 설정 (auto-fail 온/오프)
    private var _settings by mutableStateOf(Settings())
    val settings: Settings get() = _settings

    companion object {
        private const val DAY_MILLIS = 24L * 60L * 60L * 1000L   // 24시간
    }

    init {
        // 1) 설정 로드
        _settings = SettingsStorage.load(app)

        // 2) 저장된 퀘스트/히스토리 로드
        val (loadedQuests, loadedHistory) = QuestStorage.load(getApplication())
        _quests.addAll(loadedQuests)
        _history.addAll(loadedHistory)

        // 3) 설정이 켜져 있으면 24시간 지난 ACTIVE 퀘스트 자동 실패 처리
        if (_settings.autoFailAfter24h) {
            autoFailExpiredQuests()
        }

        // 4) 타이머 루프 시작
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                tick()
            }
        }
    }

    /** SettingsScreen에서 호출: 설정 변경 + 저장 + auto-fail 즉시 반영 */
    fun updateSettings(newSettings: Settings) {
        _settings = newSettings
        SettingsStorage.save(getApplication(), _settings)

        if (_settings.autoFailAfter24h) {
            // 방금 켰다면, 한 번 더 24시간 지난 애들 정리
            autoFailExpiredQuests()
        }
    }

    /** 24시간 넘은 ACTIVE 퀘스트들을 FAILED로 옮기기 */
    private fun autoFailExpiredQuests() {
        val now = System.currentTimeMillis()
        val iterator = _quests.iterator()
        val toAdd = mutableListOf<Quest>()

        while (iterator.hasNext()) {
            val q = iterator.next()
            if (q.status == QuestStatus.ACTIVE) {
                val age = now - q.createdAt
                if (age >= DAY_MILLIS) {
                    iterator.remove()
                    toAdd.add(
                        q.copy(
                            status = QuestStatus.FAILED,
                            isRunning = false,
                            failedAt = now
                        )
                    )
                }
            }
        }

        if (toAdd.isNotEmpty()) {
            _history.addAll(0, toAdd)
            persist()
        }
    }

    /** 1초마다 실행되면서 타이머 켜져 있는 퀘스트 시간 증가 */
    private fun tick() {
        _quests.forEachIndexed { index, quest ->
            if (quest.isRunning && quest.status == QuestStatus.ACTIVE) {
                val updated = quest.copy(elapsedMillis = quest.elapsedMillis + 1000L)
                _quests[index] = updated
            }
        }
    }

    /** 현재 상태를 SharedPreferences에 저장 */
    private fun persist() {
        QuestStorage.save(
            getApplication(),
            _quests,
            _history
        )
    }

    /** 새 퀘스트 추가 */
    fun addQuest(title: String) {
        if (title.isBlank()) return
        _quests.add(0, Quest(title = title.trim()))
        persist()
    }

    /** 타이머 시작/정지 토글 */
    fun toggleTimer(quest: Quest) {
        val index = _quests.indexOfFirst { it.id == quest.id }
        if (index == -1) return
        val current = _quests[index]
        if (current.status != QuestStatus.ACTIVE) return
        _quests[index] = current.copy(isRunning = !current.isRunning)
        persist()
    }

    /** 완료 처리: 오늘의 목록에서 제거 + 히스토리(완료)로 이동 */
    fun completeQuest(quest: Quest) {
        val index = _quests.indexOfFirst { it.id == quest.id }
        if (index == -1) return
        val current = _quests[index]
        if (current.status != QuestStatus.ACTIVE) return

        val updated = current.copy(
            status = QuestStatus.COMPLETED,
            isRunning = false,
            completedAt = System.currentTimeMillis()
        )
        _quests.removeAt(index)
        _history.add(0, updated)
        persist()
    }

    /** 실패 처리: 오늘의 목록에서 제거 + 히스토리(실패)로 이동 */
    fun failQuest(quest: Quest) {
        val index = _quests.indexOfFirst { it.id == quest.id }
        if (index == -1) return
        val current = _quests[index]
        val updated = current.copy(
            status = QuestStatus.FAILED,
            isRunning = false,
            failedAt = System.currentTimeMillis()
        )
        _quests.removeAt(index)
        _history.add(0, updated)
        persist()
    }

    /** 미완료(Active) 퀘스트 전체 실패 처리 – 휴지통 버튼 기능 */
    fun clearIncomplete() {
        val iterator = _quests.iterator()
        val toAdd = mutableListOf<Quest>()
        val now = System.currentTimeMillis()

        while (iterator.hasNext()) {
            val q = iterator.next()
            if (q.status == QuestStatus.ACTIVE) {
                iterator.remove()
                toAdd.add(
                    q.copy(
                        status = QuestStatus.FAILED,
                        isRunning = false,
                        failedAt = now
                    )
                )
            }
        }
        if (toAdd.isNotEmpty()) {
            _history.addAll(0, toAdd)
            persist()
        }
    }
}
