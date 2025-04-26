package com.photobuddy.utils


import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeHelper {

    const val LIGHT_MODE = "light"
    const val DARK_MODE = "dark"
    const val DEFAULT_MODE = "default"

    fun applyTheme(themePref: String) {
        when (themePref) {
            LIGHT_MODE -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            DARK_MODE -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    fun saveTheme(context: Context, selectedTheme: String) {
        val sharedPref = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().putString("selected_theme", selectedTheme).apply()
    }

    fun getSavedTheme(context: Context): String {
        val sharedPref = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        return sharedPref.getString("selected_theme", DEFAULT_MODE) ?: DEFAULT_MODE
    }
}