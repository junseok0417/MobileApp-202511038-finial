package com.example.tlrksrma

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: QuestAdapter
    private lateinit var recyclerQuests: RecyclerView
    private lateinit var layoutEmpty: LinearLayout
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var toolbar: MaterialToolbar

    // 오늘의 퀘스트 리스트 (Active만)
    private val questList = mutableListOf<Quest>()

    // 1초마다 타이머 증가용
    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            questList.forEach { quest ->
                if (quest.isRunning && quest.status == QuestStatus.ACTIVE) {
                    quest.elapsedMillis += 1000L
                }
            }
            adapter.updateAll()
            handler.postDelayed(this, 1000L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.topAppBar)
        recyclerQuests = findViewById(R.id.recyclerQuests)
        layoutEmpty = findViewById(R.id.layoutEmptyState)
        fabAdd = findViewById(R.id.fabAddQuest)

        setSupportActionBar(toolbar)

        // 메뉴 버튼 이벤트
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_add -> {
                    showAddQuestDialog()
                    true
                }
                R.id.action_clear_incomplete -> {
                    clearIncompleteQuests()
                    true
                }
                R.id.action_history -> {
                    // TODO: 나중에 HistoryActivity 연결
                    true
                }
                else -> false
            }
        }

        // RecyclerView 설정
        adapter = QuestAdapter(
            questList,
            onToggleTimer = { quest -> toggleTimer(quest) },
            onComplete = { quest -> completeQuest(quest) },
            onLongPressDelete = { quest -> confirmDelete(quest) }
        )
        recyclerQuests.layoutManager = LinearLayoutManager(this)
        recyclerQuests.adapter = adapter

        // + 버튼
        fabAdd.setOnClickListener {
            showAddQuestDialog()
        }

        updateEmptyState()

        // 타이머 루프 시작
        handler.postDelayed(timerRunnable, 1000L)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(timerRunnable)
    }

    // 퀘스트 추가
    private fun addQuest(title: String) {
        val quest = Quest(title = title)
        questList.add(0, quest)
        adapter.notifyItemInserted(0)
        recyclerQuests.scrollToPosition(0)
        updateEmptyState()
    }

    private fun updateEmptyState() {
        if (questList.isEmpty()) {
            layoutEmpty.visibility = android.view.View.VISIBLE
            recyclerQuests.visibility = android.view.View.GONE
        } else {
            layoutEmpty.visibility = android.view.View.GONE
            recyclerQuests.visibility = android.view.View.VISIBLE
        }
    }

    // 타이머 토글
    private fun toggleTimer(quest: Quest) {
        if (quest.status != QuestStatus.ACTIVE) return
        quest.isRunning = !quest.isRunning
        adapter.updateAll()
    }

    // 완료 처리
    private fun completeQuest(quest: Quest) {
        if (quest.status != QuestStatus.ACTIVE) return
        quest.status = QuestStatus.COMPLETED
        quest.isRunning = false
        quest.completedAt = System.currentTimeMillis()
        adapter.updateAll()
        // TODO: 나중에 history 리스트로 이동 처리
    }

    // 미완료 전체 실패 처리
    private fun clearIncompleteQuests() {
        val iterator = questList.iterator()
        var changed = false
        while (iterator.hasNext()) {
            val q = iterator.next()
            if (q.status == QuestStatus.ACTIVE) {
                q.status = QuestStatus.FAILED
                q.isRunning = false
                q.failedAt = System.currentTimeMillis()
                iterator.remove()
                changed = true
            }
        }
        if (changed) {
            adapter.updateAll()
            updateEmptyState()
        }
    }

    // 삭제 확인
    private fun confirmDelete(quest: Quest) {
        AlertDialog.Builder(this)
            .setTitle("Delete Quest")
            .setMessage("Mark this quest as failed and remove it?")
            .setPositiveButton("Delete") { _, _ ->
                quest.status = QuestStatus.FAILED
                quest.isRunning = false
                quest.failedAt = System.currentTimeMillis()
                adapter.removeQuest(quest)
                updateEmptyState()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // 새 퀘스트 추가 다이얼로그
    private fun showAddQuestDialog() {
        val editText = EditText(this).apply {
            hint = "Enter quest title"
        }

        AlertDialog.Builder(this)
            .setTitle("Add New Quest")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val text = editText.text.toString().trim()
                if (text.isNotEmpty()) {
                    addQuest(text)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
