package com.example.studspacing.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.studspacing.data.model.CalculationResult
import com.example.studspacing.data.model.SavedCalculation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repository responsible for persisting and managing calculation history.
 * Utilizes SharedPreferences with JSON serialization for lightweight,
 * offline-first, schema-free local storage.
 */
class HistoryRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault())

    companion object {
        private const val PREFS_NAME = "stud_spacing_history"
        private const val KEY_CALCULATIONS = "key_saved_calculations"
    }

    /**
     * Retrieves all saved calculations ordered newest first.
     */
    @Synchronized
    fun getHistory(): List<SavedCalculation> {
        val json = prefs.getString(KEY_CALCULATIONS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<SavedCalculation>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Persists a newly calculated result to history.
     */
    @Synchronized
    fun saveCalculation(result: CalculationResult): SavedCalculation {
        val currentList = getHistory().toMutableList()
        val savedItem = SavedCalculation(
            wallLength = result.wallLength,
            spacing = result.spacing,
            thickness = result.thickness,
            unit = result.unit,
            totalStuds = result.totalStuds,
            formattedDate = dateFormat.format(Date(result.timestamp)),
            studPositions = result.studPositions
        )
        // Add to top of list
        currentList.add(0, savedItem)
        saveList(currentList)
        return savedItem
    }

    /**
     * Checks if a calculation with identical dimensions is already the most recent item.
     */
    @Synchronized
    fun isAlreadySaved(result: CalculationResult): Boolean {
        val history = getHistory()
        return history.any {
            it.wallLength == result.wallLength &&
                    it.spacing == result.spacing &&
                    it.thickness == result.thickness &&
                    it.unit == result.unit
        }
    }

    /**
     * Removes an item by its unique ID.
     */
    @Synchronized
    fun deleteCalculation(id: String) {
        val currentList = getHistory().toMutableList()
        currentList.removeAll { it.id == id }
        saveList(currentList)
    }

    /**
     * Clears all history items.
     */
    @Synchronized
    fun clearAll() {
        prefs.edit().remove(KEY_CALCULATIONS).apply()
    }

    private fun saveList(list: List<SavedCalculation>) {
        val json = gson.toJson(list)
        prefs.edit().putString(KEY_CALCULATIONS, json).apply()
    }
}
