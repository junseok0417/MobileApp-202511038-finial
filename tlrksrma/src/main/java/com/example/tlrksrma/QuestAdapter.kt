package com.example.tlrksrma

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class QuestAdapter(
    private val items: MutableList<Quest>,
    private val onToggleTimer: (Quest) -> Unit,
    private val onComplete: (Quest) -> Unit,
    private val onLongPressDelete: (Quest) -> Unit
) : RecyclerView.Adapter<QuestAdapter.QuestViewHolder>() {

    inner class QuestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.cardQuest)
        val btnToggleTimer: ImageButton = view.findViewById(R.id.btnToggleTimer)
        val btnComplete: ImageButton = view.findViewById(R.id.btnComplete)
        val textTitle: TextView = view.findViewById(R.id.textTitle)
        val textTimer: TextView = view.findViewById(R.id.textTimer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quest, parent, false)
        return QuestViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: QuestViewHolder, position: Int) {
        val quest = items[position]

        holder.textTitle.text = quest.title
        holder.textTimer.text = formatElapsedTime(quest.elapsedMillis)

        // 완료 상태면 취소선, 버튼 비활성
        if (quest.status == QuestStatus.COMPLETED) {
            holder.textTitle.paintFlags =
                holder.textTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.btnToggleTimer.isEnabled = false
            holder.btnComplete.isEnabled = false
        } else {
            holder.textTitle.paintFlags =
                holder.textTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.btnToggleTimer.isEnabled = quest.status == QuestStatus.ACTIVE
            holder.btnComplete.isEnabled = quest.status == QuestStatus.ACTIVE
        }

        // 타이머 버튼 아이콘
        if (quest.isRunning) {
            holder.btnToggleTimer.setImageResource(android.R.drawable.ic_media_pause)
        } else {
            holder.btnToggleTimer.setImageResource(android.R.drawable.ic_media_play)
        }

        holder.btnToggleTimer.setOnClickListener {
            onToggleTimer(quest)
        }

        holder.btnComplete.setOnClickListener {
            onComplete(quest)
        }

        holder.card.setOnLongClickListener {
            onLongPressDelete(quest)
            true
        }
    }

    fun addQuest(quest: Quest) {
        items.add(0, quest)
        notifyItemInserted(0)
    }

    fun removeQuest(quest: Quest) {
        val index = items.indexOf(quest)
        if (index != -1) {
            items.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun updateAll() {
        notifyDataSetChanged()
    }
}

fun formatElapsedTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}