package com.example.tlrks

import android.app.Application
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object QuestStorage {

    private const val PREF_NAME = "daily_quest_data"
    private const val KEY_QUESTS = "quests"
    private const val KEY_HISTORY = "history"

    fun save(app: Application, quests: List<Quest>, history: List<Quest>) {
        val prefs = app.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val questsJson = JSONArray()
        quests.forEach { q ->
            questsJson.put(questToJson(q))
        }

        val historyJson = JSONArray()
        history.forEach { q ->
            historyJson.put(questToJson(q))
        }

        prefs.edit()
            .putString(KEY_QUESTS, questsJson.toString())
            .putString(KEY_HISTORY, historyJson.toString())
            .apply()
    }

    fun load(app: Application): Pair<List<Quest>, List<Quest>> {
        val prefs = app.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val questsStr = prefs.getString(KEY_QUESTS, null)
        val historyStr = prefs.getString(KEY_HISTORY, null)

        val quests = mutableListOf<Quest>()
        val history = mutableListOf<Quest>()

        if (!questsStr.isNullOrBlank()) {
            val arr = JSONArray(questsStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                quests.add(jsonToQuest(obj))
            }
        }

        if (!historyStr.isNullOrBlank()) {
            val arr = JSONArray(historyStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                history.add(jsonToQuest(obj))
            }
        }

        return quests to history
    }

    private fun questToJson(q: Quest): JSONObject {
        val obj = JSONObject()
        obj.put("id", q.id)
        obj.put("title", q.title)
        obj.put("createdAt", q.createdAt)
        obj.put("elapsedMillis", q.elapsedMillis)
        obj.put("isRunning", q.isRunning)
        obj.put("status", q.status.name)
        obj.put("completedAt", q.completedAt ?: JSONObject.NULL)
        obj.put("failedAt", q.failedAt ?: JSONObject.NULL)
        return obj
    }

    private fun jsonToQuest(obj: JSONObject): Quest {
        return Quest(
            id = obj.getLong("id"),
            title = obj.getString("title"),
            createdAt = obj.getLong("createdAt"),
            elapsedMillis = obj.getLong("elapsedMillis"),
            isRunning = obj.getBoolean("isRunning"),
            status = QuestStatus.valueOf(obj.getString("status")),
            completedAt = if (obj.isNull("completedAt")) null else obj.getLong("completedAt"),
            failedAt = if (obj.isNull("failedAt")) null else obj.getLong("failedAt")
        )
    }
}
