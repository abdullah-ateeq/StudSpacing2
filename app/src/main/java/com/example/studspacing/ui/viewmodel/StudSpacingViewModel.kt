package com.example.studspacing.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.studspacing.data.model.CalculationResult
import com.example.studspacing.data.model.SavedCalculation
import com.example.studspacing.data.repository.HistoryRepository
import com.example.studspacing.data.repository.PreferencesRepository
import com.example.studspacing.util.StudCalculator

/**
 * Shared ViewModel managing calculations, input state, and history.
 */
class StudSpacingViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = PreferencesRepository(application)
    private val historyRepository = HistoryRepository(application)

    // Current calculation result
    private val _currentResult = MutableLiveData<CalculationResult?>()
    val currentResult: LiveData<CalculationResult?> = _currentResult

    // History list
    private val _historyList = MutableLiveData<List<SavedCalculation>>()
    val historyList: LiveData<List<SavedCalculation>> = _historyList

    // UI event messages / feedback
    private val _feedbackMessage = MutableLiveData<String?>()
    val feedbackMessage: LiveData<String?> = _feedbackMessage

    init {
        loadHistory()
    }

    fun isMetric(): Boolean = preferencesRepository.isMetric

    fun getUnitSymbol(): String = preferencesRepository.getUnitSymbol()

    fun getDefaultSpacing(): Double = preferencesRepository.getDefaultSpacing()

    fun getDefaultThickness(): Double = preferencesRepository.getDefaultThickness()

    fun getDefaultWallLength(): Double = preferencesRepository.getDefaultWallLength()

    fun loadHistory() {
        _historyList.value = historyRepository.getHistory()
    }

    /**
     * Executes the framing calculation and updates [_currentResult].
     */
    fun performCalculation(wallLength: Double, spacing: Double, thickness: Double): Boolean {
        return try {
            val unit = getUnitSymbol()
            val result = StudCalculator.calculate(
                wallLength = wallLength,
                spacing = spacing,
                thickness = thickness,
                unit = unit
            )
            _currentResult.value = result
            true
        } catch (e: Exception) {
            _feedbackMessage.value = e.localizedMessage
            false
        }
    }

    /**
     * Sets the active result from a selected history item.
     */
    fun loadCalculationFromHistory(saved: SavedCalculation) {
        val result = CalculationResult(
            wallLength = saved.wallLength,
            spacing = saved.spacing,
            thickness = saved.thickness,
            unit = saved.unit,
            totalStuds = saved.totalStuds,
            studPositions = saved.studPositions
        )
        _currentResult.value = result
    }

    /**
     * Saves the current calculation into the history repository.
     */
    fun saveCurrentCalculation(): Boolean {
        val result = _currentResult.value ?: return false
        historyRepository.saveCalculation(result)
        loadHistory()
        return true
    }

    fun isCurrentResultAlreadySaved(): Boolean {
        val result = _currentResult.value ?: return false
        return historyRepository.isAlreadySaved(result)
    }

    fun deleteHistoryItem(id: String) {
        historyRepository.deleteCalculation(id)
        loadHistory()
    }

    fun clearAllHistory() {
        historyRepository.clearAll()
        loadHistory()
    }

    fun clearFeedbackMessage() {
        _feedbackMessage.value = null
    }
}
