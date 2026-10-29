package com.example.studspacing.data.model

import java.io.Serializable
import java.util.UUID

/**
 * Encapsulates a calculation persisted in local storage history.
 *
 * @property id Unique identifier for this saved calculation
 * @property wallLength Total length of the wall
 * @property spacing Desired on-center (O.C.) spacing
 * @property thickness Thickness of framing lumber
 * @property unit Measurement unit ("in" or "cm")
 * @property totalStuds Total count of studs required
 * @property formattedDate User-readable timestamp string (e.g., "Oct 29, 2026 14:30")
 * @property studPositions List of stud positions calculated
 */
data class SavedCalculation(
    val id: String = UUID.randomUUID().toString(),
    val wallLength: Double,
    val spacing: Double,
    val thickness: Double,
    val unit: String,
    val totalStuds: Int,
    val formattedDate: String,
    val studPositions: List<StudPosition>
) : Serializable
