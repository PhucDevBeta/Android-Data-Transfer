package com.example.android_data_transfer.utils.language

import android.content.Context
import android.content.SharedPreferences
import com.example.android_data_transfer.models.local.LANG

object ManagerSaveLocal {
    private const val PREF_NAME = "gps_preferences"
    private const val KEY_LANGUAGE = "app_language"
    private const val KEY_FIRST_OPEN = "is_first_open"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setLanguageApp(lang: LANG) {
        sharedPreferences.edit().putString(KEY_LANGUAGE, lang.name).apply()
    }

    fun getLanguageApp(): LANG? {
        val langString = sharedPreferences.getString(KEY_LANGUAGE, null) ?: return null
        return try {
            LANG.valueOf(langString)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun setFirstOpen(isFirstOpen: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_FIRST_OPEN, isFirstOpen).apply()
    }

    fun getFirstOpen(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_OPEN, true)
    }
}