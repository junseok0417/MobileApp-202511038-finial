package com.example.tlrks

import android.app.Application
import android.content.Context

object SettingsStorage {

    private const val PREF_NAME = "daily_quest_settings"
    private const val KEY_AUTO_FAIL = "auto_fail_24h"

    fun load(app: Application): Settings {
        val prefs = app.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val autoFail = prefs.getBoolean(KEY_AUTO_FAIL, true)
        return Settings(autoFailAfter24h = autoFail)
    }

    fun save(app: Application, settings: Settings) {
        val prefs = app.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_AUTO_FAIL, settings.autoFailAfter24h)
            .apply()
    }
}
