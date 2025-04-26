package com.photobuddy.ui


import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import com.photobuddy.R
import com.photobuddy.base.BaseActivity
import com.photobuddy.utils.ThemeHelper


class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val themePreference = findPreference<androidx.preference.ListPreference>("theme")
            themePreference?.setOnPreferenceChangeListener { _, newValue ->
                when (newValue) {
                    "light" -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        ThemeHelper.saveTheme(requireContext(), ThemeHelper.LIGHT_MODE)
                    }
                    "dark" -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        ThemeHelper.saveTheme(requireContext(), ThemeHelper.DARK_MODE)
                    }
                    else -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        ThemeHelper.saveTheme(requireContext(), ThemeHelper.DEFAULT_MODE)
                    }
                }
                true
            }
        }
    }
}