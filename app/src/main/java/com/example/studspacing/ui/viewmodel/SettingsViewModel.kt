package com.example.studspacing.ui.viewmodel

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.studspacing.data.repository.PreferencesRepository

/**
 * ViewModel managing settings state (unit system & dark mode theme).
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PreferencesRepository(application)

    private val _isMetric = MutableLiveData(repository.isMetric)
    val isMetric: LiveData<Boolean> = _isMetric

    private val _isDarkMode = MutableLiveData(repository.isDarkMode)
    val isDarkMode: LiveData<Boolean> = _isDarkMode

    fun setMetric(enabled: Boolean) {
        repository.isMetric = enabled
        _isMetric.value = enabled
    }

    fun setDarkMode(enabled: Boolean) {
        repository.isDarkMode = enabled
        _isDarkMode.value = enabled
        applyTheme(enabled)
    }

    fun applySavedTheme() {
        applyTheme(repository.isDarkMode)
    }

    private fun applyTheme(isDark: Boolean) {
        val mode = if (isDark) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
