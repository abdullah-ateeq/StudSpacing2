package com.example.studspacing.data.repository

import android.content.Context
import android.content.SharedPreferences

/**
 * Repository responsible for user preferences (Units & Theme).
 */
class PreferencesRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "stud_spacing_preferences"
        private const val KEY_IS_METRIC = "key_is_metric"
        private const val KEY_IS_DARK_MODE = "key_is_dark_mode"
    }

    /**
     * True if the selected unit is Metric (centimeters); False if Imperial (inches).
     * Defaults to Imperial (standard for framing carpentry).
     */
    var isMetric: Boolean
        get() = prefs.getBoolean(KEY_IS_METRIC, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_METRIC, value).apply()

    /**
     * True if Dark Mode is explicitly enabled by the user; False for Light Mode.
     */
    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_IS_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_DARK_MODE, value).apply()

    fun getUnitSymbol(): String = if (isMetric) "cm" else "in"

    fun getDefaultSpacing(): Double = if (isMetric) 40.0 else 16.0

    fun getDefaultThickness(): Double = if (isMetric) 3.8 else 1.5

    fun getDefaultWallLength(): Double = if (isMetric) 300.0 else 120.0
}
